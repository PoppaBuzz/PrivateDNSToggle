package com.jphat.quickdns.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.widget.RemoteViews
import com.jphat.quickdns.DNSProviderActivity
import com.jphat.quickdns.R

class PrivateDNSWidget : AppWidgetProvider() {
    
    companion object {
        const val ACTION_TOGGLE_DNS = "com.jphat.quickdns.ACTION_TOGGLE_DNS"
        const val ACTION_OPEN_SETTINGS = "com.jphat.quickdns.ACTION_OPEN_SETTINGS"
        const val PREFS_NAME = "PrivateDNSPrefs"
        const val CURRENT_PROVIDER_KEY = "current_provider"
    }
    
    private val dnsProviders = mapOf(
        "AdGuard" to "dns.adguard.com",
        "Cloudflare" to "1dot1dot1dot1.cloudflare-dns.com",
        "Google" to "dns.google",
        "NextDNS" to "dns.nextdns.io",
        "Quad9" to "dns.quad9.net",
        "LibreDNS" to "dot.libredns.gr",
        "Mullvad" to "dns.mullvad.net",
        "OpenDNS" to "dns.opendns.com",
        "CleanBrowsing" to "security-filter-dns.cleanbrowsing.org",
        "Control D" to "freedns.controld.com"
    )

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, options)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_TOGGLE_DNS -> {
                togglePrivateDNS(context)
                // Broadcast update to all widget types via coordinator
                WidgetUpdateCoordinator.broadcastWidgetUpdate(context)
            }
            ACTION_OPEN_SETTINGS -> {
                val settingsIntent = Intent(context, DNSProviderActivity::class.java)
                settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(settingsIntent)
            }
        }
    }
    
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Start listening for DNS changes
        updateAllWidgets(context)
    }
    
    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            android.content.ComponentName(context, PrivateDNSWidget::class.java)
        )
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        options: Bundle? = null
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_private_dns)
        
        val mode = getPrivateDNSMode(context)
        val isEnabled = mode != "off"
        
        if (isEnabled) {
            views.setImageViewResource(R.id.widgetLogo, R.drawable.quickdns_new_on)
            views.setTextViewText(R.id.widgetStatus, "ON")
            views.setTextColor(R.id.widgetStatus, android.graphics.Color.parseColor("#0de2d2"))
        } else {
            views.setImageViewResource(R.id.widgetLogo, R.drawable.quickdns_new_off)
            views.setTextViewText(R.id.widgetStatus, "OFF")
            views.setTextColor(R.id.widgetStatus, android.graphics.Color.parseColor("#888888"))
        }
        
        val currentWidth = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 40) ?: 40
        val scale = if (currentWidth > 80) (currentWidth.toFloat() / 80f).coerceAtMost(2.5f) else 1f
        val statusTextSize = (10f * scale).coerceAtMost(28f)
        views.setTextViewTextSize(R.id.widgetStatus, TypedValue.COMPLEX_UNIT_SP, statusTextSize)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val iconSize = (24f * scale).toInt().coerceIn(24, 64)
            val iconPadding = (2f * scale).toInt().coerceIn(2, 8)
            val iconPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, iconSize.toFloat(), context.resources.displayMetrics).toInt()
            val padPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, iconPadding.toFloat(), context.resources.displayMetrics).toInt()
            views.setInt(R.id.widgetSettingsButton, "setMinimumWidth", iconPx)
            views.setInt(R.id.widgetSettingsButton, "setMinimumHeight", iconPx)
            views.setViewPadding(R.id.widgetSettingsButton, padPx, padPx, padPx, padPx)
        }
        
        // Toggle button click
        val toggleIntent = Intent(context, PrivateDNSWidget::class.java)
        toggleIntent.action = ACTION_TOGGLE_DNS
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetToggleButton, togglePendingIntent)
        
        // Settings button click
        val settingsIntent = Intent(context, PrivateDNSWidget::class.java)
        settingsIntent.action = ACTION_OPEN_SETTINGS
        val settingsPendingIntent = PendingIntent.getBroadcast(
            context, 1, settingsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetSettingsButton, settingsPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun togglePrivateDNS(context: Context) {
        try {
            val mode = getPrivateDNSMode(context)
            
            if (mode == "off") {
                // Enable with current provider
                val currentProvider = getCurrentProvider(context)
                Settings.Global.putString(context.contentResolver, "private_dns_mode", "hostname")
                Settings.Global.putString(context.contentResolver, "private_dns_specifier", currentProvider)
            } else {
                // Disable
                Settings.Global.putString(context.contentResolver, "private_dns_mode", "off")
            }
        } catch (e: SecurityException) {
            // Permission denied - show toast and open app to grant permission
            android.widget.Toast.makeText(context, "Permission needed - opening app", android.widget.Toast.LENGTH_SHORT).show()
            val intent = Intent(context, com.jphat.quickdns.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPrivateDNSMode(context: Context): String {
        return try {
            Settings.Global.getString(context.contentResolver, "private_dns_mode") ?: "off"
        } catch (e: Exception) {
            "off"
        }
    }

    private fun getCurrentProvider(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_PROVIDER_KEY, dnsProviders.values.first()) 
            ?: dnsProviders.values.first()
    }

    private fun getCurrentProviderName(context: Context): String {
        val currentProvider = getCurrentProvider(context)
        return dnsProviders.entries.find { it.value == currentProvider }?.key ?: "Custom"
    }
}
