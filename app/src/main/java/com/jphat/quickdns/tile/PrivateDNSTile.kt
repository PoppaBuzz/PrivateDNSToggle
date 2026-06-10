package com.jphat.quickdns.tile

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.edit

class PrivateDNSTile : TileService() {
    companion object {
        const val PREFS_NAME = "PrivateDNSPrefs"
        const val CURRENT_PROVIDER_KEY = "current_provider"
        const val CUSTOM_PROVIDERS_KEY = "custom_providers"
    }

    private val defaultProviders = mapOf(
        "AdGuard (Privacy)" to "dns.adguard.com",
        "Cloudflare (Fast)" to "1dot1dot1dot1.cloudflare-dns.com",
        "Google DNS" to "dns.google",
        "NextDNS (Security)" to "dns.nextdns.io",
        "Quad9 (Threat Protection)" to "dns.quad9.net",
        "DNS0 (Privacy)" to "0.dns0.eu",
        "DNS.SB (No Logs)" to "dns.sb",
        "LibreDNS (No Logs)" to "dot.libredns.gr",
        "Mullvad (Privacy)" to "dns.mullvad.net",
        "OpenDNS (Cisco)" to "dns.opendns.com",
        "CleanBrowsing (Family)" to "family-filter-dns.cleanbrowsing.org",
        "CleanBrowsing (Security)" to "security-filter-dns.cleanbrowsing.org",
        "Control D (Customizable)" to "freedns.controld.com"
    )

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        val mode = getPrivateDNSMode()
        if (mode == "off") {
            togglePrivateDNS()
        } else {
            showDisableOrChangeDialog()
        }
    }

    private fun togglePrivateDNS() {
        try {
            val mode = getPrivateDNSMode()
            
            if (mode == "off") {
                // Enable with current/default provider
                val currentProvider = getCurrentProvider()
                Settings.Global.putString(contentResolver, "private_dns_mode", "hostname")
                Settings.Global.putString(contentResolver, "private_dns_specifier", currentProvider)
            } else {
                // Disable
                Settings.Global.putString(contentResolver, "private_dns_mode", "off")
            }
            
            // Update tile and widgets after a short delay to allow system to update
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                updateTile()
                updateWidgets()
            }, 200)
            
        } catch (e: SecurityException) {
            // Permission denied - open app to grant permission
            val intent = Intent(this, com.jphat.quickdns.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateWidgets() {
        // Update 1x1 shield widget
        val intentShield = Intent(this, com.jphat.quickdns.widget.PrivateDNSWidget::class.java)
        intentShield.action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val idsShield = android.appwidget.AppWidgetManager.getInstance(this).getAppWidgetIds(
            android.content.ComponentName(this, com.jphat.quickdns.widget.PrivateDNSWidget::class.java)
        )
        intentShield.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, idsShield)
        sendBroadcast(intentShield)
        
        // Update 1x1 tile widget
        val intentTile = Intent(this, com.jphat.quickdns.widget.PrivateDNSWidgetTile::class.java)
        intentTile.action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val idsTile = android.appwidget.AppWidgetManager.getInstance(this).getAppWidgetIds(
            android.content.ComponentName(this, com.jphat.quickdns.widget.PrivateDNSWidgetTile::class.java)
        )
        intentTile.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, idsTile)
        sendBroadcast(intentTile)
        
        // Update 2x1 widgets
        val intent2x1 = Intent(this, com.jphat.quickdns.widget.PrivateDNSWidgetLarge::class.java)
        intent2x1.action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids2x1 = android.appwidget.AppWidgetManager.getInstance(this).getAppWidgetIds(
            android.content.ComponentName(this, com.jphat.quickdns.widget.PrivateDNSWidgetLarge::class.java)
        )
        intent2x1.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids2x1)
        sendBroadcast(intent2x1)
    }

    private fun setPrivateDNSProvider(provider: String) {
        try {
            Settings.Global.putString(contentResolver, "private_dns_specifier", provider)
            Settings.Global.putString(contentResolver, "private_dns_mode", "hostname")
            saveCurrentProvider(provider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disablePrivateDNS() {
        try {
            Settings.Global.putString(contentResolver, "private_dns_mode", "off")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPrivateDNSMode(): String {
        return try {
            Settings.Global.getString(
                contentResolver,
                "private_dns_mode"
            ) ?: "off"
        } catch (e: Exception) {
            "off"
        }
    }

    private fun getAllProviders(): Map<String, String> {
        val providers = defaultProviders.toMutableMap()
        providers.putAll(getCustomProviders())
        return providers
    }

    private fun getCustomProviders(): Map<String, String> {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val customProvidersJson = prefs.getString(CUSTOM_PROVIDERS_KEY, "") ?: ""
        if (customProvidersJson.isEmpty()) return emptyMap()
        
        return customProvidersJson.split("|").mapNotNull { entry ->
            val parts = entry.split(":::")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
    }

    private fun getCurrentProvider(): String {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_PROVIDER_KEY, defaultProviders.values.first()) ?: defaultProviders.values.first()
    }

    private fun saveCurrentProvider(provider: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(CURRENT_PROVIDER_KEY, provider) }
    }

    private fun showDisableOrChangeDialog() {
        val dialog = android.app.AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Private DNS")
            .setPositiveButton("Turn Off") { _, _ ->
                disablePrivateDNS()
                updateTile()
                updateWidgets()
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    updateTile()
                }, 300)
            }
            .setNegativeButton("Change Provider") { _, _ -> openProviderSelection() }
            .setNeutralButton("Cancel", null)
            .create()
        showDialog(dialog)
    }

    private fun openProviderSelection() {
        val intent = Intent(this, com.jphat.quickdns.DNSProviderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun updateTile() {
        val mode = getPrivateDNSMode()
        val tile = qsTile ?: return

        tile.label = "quickDNS"
        
        if (mode == "off") {
            tile.state = Tile.STATE_INACTIVE
            tile.icon = android.graphics.drawable.Icon.createWithResource(this, com.jphat.quickdns.R.drawable.ic_shield_inactive)
        } else {
            tile.state = Tile.STATE_ACTIVE
            tile.icon = android.graphics.drawable.Icon.createWithResource(this, com.jphat.quickdns.R.drawable.ic_shield_active)
        }
        
        val provider = if (mode == "off") {
            "Off"
        } else {
            val current = getCurrentProvider()
            val allProviders = getAllProviders()
            allProviders.entries.find { it.value == current }?.key?.split(" ")?.get(0) ?: "On"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = provider
        }
        tile.updateTile()
    }
}
