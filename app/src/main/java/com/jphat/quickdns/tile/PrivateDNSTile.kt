package com.jphat.quickdns.tile

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

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
        "LibreDNS (No Logs)" to "dot.libredns.gr",
        "Mullvad (Privacy)" to "dns.mullvad.net",
        "OpenDNS (Cisco)" to "dns.opendns.com",
        "CleanBrowsing (Family)" to "security-filter-dns.cleanbrowsing.org",
        "Control D (Customizable)" to "freedns.controld.com"
    )

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        togglePrivateDNS()
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
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateWidgets() {
        // Update 1x1 widgets
        val intent1x1 = Intent(this, com.jphat.quickdns.widget.PrivateDNSWidget::class.java)
        intent1x1.action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids1x1 = android.appwidget.AppWidgetManager.getInstance(this).getAppWidgetIds(
            android.content.ComponentName(this, com.jphat.quickdns.widget.PrivateDNSWidget::class.java)
        )
        intent1x1.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids1x1)
        sendBroadcast(intent1x1)
        
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
        prefs.edit().putString(CURRENT_PROVIDER_KEY, provider).apply()
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
        tile.subtitle = provider
        tile.updateTile()
    }
}
