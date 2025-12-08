package com.jphat.privatednstoggle.tile

import android.content.Context
import android.content.Intent
import android.os.Build
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
        val mode = getPrivateDNSMode()
        
        if (mode == "off") {
            // Enable with current/default provider
            enablePrivateDNS()
        } else {
            // Open provider selection activity
            val intent = Intent(this, com.jphat.privatednstoggle.DNSProviderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        updateTile()
    }

    private fun enablePrivateDNS() {
        try {
            val currentProvider = getCurrentProvider()
            Settings.Global.putString(
                contentResolver,
                "private_dns_mode",
                "hostname"
            )
            Settings.Global.putString(
                contentResolver,
                "private_dns_specifier",
                currentProvider
            )
        } catch (e: SecurityException) {
            // Permission denied - open app to grant Shizuku permission
            val intent = Intent(this, com.jphat.privatednstoggle.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setPrivateDNSProvider(provider: String) {
        try {
            Settings.Global.putString(
                contentResolver,
                "private_dns_specifier",
                provider
            )
            Settings.Global.putString(
                contentResolver,
                "private_dns_mode",
                "hostname"
            )
            saveCurrentProvider(provider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disablePrivateDNS() {
        try {
            Settings.Global.putString(
                contentResolver,
                "private_dns_mode",
                "off"
            )
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

        tile.label = "Private DNS"
        tile.state = if (mode == "off") Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
        
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
