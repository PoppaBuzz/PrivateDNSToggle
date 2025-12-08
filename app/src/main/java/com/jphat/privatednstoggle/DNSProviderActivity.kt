package com.jphat.privatednstoggle

import android.app.AlertDialog
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DNSProviderActivity : AppCompatActivity() {
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

    private lateinit var listView: ListView
    private lateinit var adapter: ProviderAdapter
    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dns_provider)
        
        listView = findViewById(R.id.dnsProviderList)
        val disableButton = findViewById<Button>(R.id.disableButton)
        val addProviderButton = findViewById<Button>(R.id.addProviderButton)
        
        setupProviderList()
        
        disableButton.setOnClickListener {
            disablePrivateDNS()
            Toast.makeText(this, "Private DNS Disabled", Toast.LENGTH_SHORT).show()
            finish()
        }

        addProviderButton.setOnClickListener {
            showAddProviderDialog()
        }
    }

    private fun setupProviderList() {
        val allProviders = getAllProviders()
        adapter = ProviderAdapter(allProviders)
        listView.adapter = adapter
        
        // Set current selected provider
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentProvider = prefs.getString(CURRENT_PROVIDER_KEY, defaultProviders.values.first()) 
            ?: defaultProviders.values.first()
        selectedPosition = allProviders.indexOfFirst { it.second == currentProvider }
        
        listView.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            val selectedProvider = allProviders[position].second
            setPrivateDNSProvider(selectedProvider)
            prefs.edit().putString(CURRENT_PROVIDER_KEY, selectedProvider).apply()
            Toast.makeText(this, "DNS Provider Changed", Toast.LENGTH_SHORT).show()
            adapter.notifyDataSetChanged()
        }
    }

    private fun getAllProviders(): MutableList<Pair<String, String>> {
        val providers = defaultProviders.toList().toMutableList()
        providers.addAll(getCustomProviders())
        return providers
    }

    private fun getCustomProviders(): List<Pair<String, String>> {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val customProvidersJson = prefs.getString(CUSTOM_PROVIDERS_KEY, "") ?: ""
        if (customProvidersJson.isEmpty()) return emptyList()
        
        return customProvidersJson.split("|").mapNotNull { entry ->
            val parts = entry.split(":::")
            if (parts.size == 2) parts[0] to parts[1] else null
        }
    }

    private fun saveCustomProviders(providers: List<Pair<String, String>>) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = providers.joinToString("|") { "${it.first}:::${it.second}" }
        prefs.edit().putString(CUSTOM_PROVIDERS_KEY, json).apply()
    }

    private fun showAddProviderDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_provider, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.providerNameInput)
        val hostnameInput = dialogView.findViewById<EditText>(R.id.providerHostnameInput)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(R.string.add) { _, _ ->
                val name = nameInput.text.toString().trim()
                val hostname = hostnameInput.text.toString().trim()
                
                if (name.isNotEmpty() && hostname.isNotEmpty()) {
                    addCustomProvider(name, hostname)
                } else {
                    Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addCustomProvider(name: String, hostname: String) {
        val customProviders = getCustomProviders().toMutableList()
        customProviders.add(name to hostname)
        saveCustomProviders(customProviders)
        
        setupProviderList()
        Toast.makeText(this, R.string.provider_added, Toast.LENGTH_SHORT).show()
    }

    private fun deleteCustomProvider(position: Int) {
        val allProviders = getAllProviders()
        val providerToDelete = allProviders[position]
        
        // Only allow deleting custom providers (not default ones)
        if (defaultProviders.containsValue(providerToDelete.second)) {
            return
        }
        
        val customProviders = getCustomProviders().toMutableList()
        customProviders.remove(providerToDelete)
        saveCustomProviders(customProviders)
        
        setupProviderList()
        Toast.makeText(this, R.string.provider_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun setPrivateDNSProvider(provider: String) {
        try {
            Settings.Global.putString(contentResolver, "private_dns_specifier", provider)
            Settings.Global.putString(contentResolver, "private_dns_mode", "hostname")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Permission Error: Grant via Shizuku", Toast.LENGTH_LONG).show()
        }
    }

    private fun disablePrivateDNS() {
        try {
            Settings.Global.putString(contentResolver, "private_dns_mode", "off")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ProviderAdapter(private val providers: List<Pair<String, String>>) : BaseAdapter() {
        override fun getCount() = providers.size
        override fun getItem(position: Int) = providers[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(this@DNSProviderActivity)
                .inflate(R.layout.list_item_provider, parent, false)
            
            val provider = providers[position]
            val radioButton = view.findViewById<RadioButton>(R.id.providerRadio)
            val nameText = view.findViewById<TextView>(R.id.providerName)
            val hostnameText = view.findViewById<TextView>(R.id.providerHostname)
            val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)
            
            radioButton.isChecked = position == selectedPosition
            nameText.text = provider.first
            hostnameText.text = provider.second
            
            // Show delete button only for custom providers
            val isCustom = !defaultProviders.containsValue(provider.second)
            deleteButton.visibility = if (isCustom) View.VISIBLE else View.GONE
            
            deleteButton.setOnClickListener {
                deleteCustomProvider(position)
            }
            
            return view
        }
    }
}
