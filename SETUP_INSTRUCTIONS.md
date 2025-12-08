# Private DNS Toggle Widget - Setup Instructions

## What Was Implemented

Your Android project now includes a fully functional Private DNS Quick Settings Tile with:

- **Quick Settings Tile** (`PrivateDNSTile.kt`) - Toggle Private DNS on/off from Quick Settings
- **DNS Provider Selection** (`DNSProviderActivity.kt`) - Choose from 6 DNS providers
- **Shizuku Integration** - System-level permissions without root
- **6 DNS Providers**:
  - AdGuard (Privacy)
  - Cloudflare (Fast)
  - Google DNS
  - NextDNS (Security)
  - Quad9 (Threat Protection)
  - LibreDNS (No Logs)

## How It Works

1. **First tap** (when OFF): Enables Private DNS with the last selected provider
2. **Second tap** (when ON): Opens provider selection activity
3. **Provider selection**: Choose a provider or disable Private DNS entirely

## Setup Steps

### 1. Install Shizuku on Your Android Device

- Download Shizuku from Google Play Store
- Open Shizuku app
- Enable Developer Options on your device:
  - Go to Settings > About Phone
  - Tap "Build Number" 7 times
- Go to Settings > Developer Options > Enable "Wireless Debugging"
- In Shizuku app, tap "Start via Wireless debugging"
- Follow the pairing instructions

### 2. Build and Install the App

```bash
# Sync Gradle dependencies
./gradlew build

# Install on connected device
./gradlew installDebug
```

### 3. Grant Shizuku Permission

- Open your app - you'll see a setup screen
- Tap "Open Shizuku App" to launch Shizuku (if not already running)
- Return to your app and tap "Request Permission"
- Grant the permission in the Shizuku dialog that appears

### 4. Add the Quick Settings Tile

- Swipe down twice from the top of your screen to open Quick Settings
- Tap the edit button (pencil icon)
- Find "Private DNS" tile and drag it to your Quick Settings
- Tap Done

### 5. Use the Tile

- **Tap once** to enable Private DNS with default provider (AdGuard)
- **Tap again** to open provider selection
- **Choose a provider** from the list
- **Tap "Disable Private DNS"** button to turn it off

## Files Created

```
app/src/main/
├── aidl/com/jphat/privatednstoggle/
│   └── IPrivateDNSUserService.aidl
├── java/com/jphat/privatednstoggle/
│   ├── tile/
│   │   └── PrivateDNSTile.kt
│   └── DNSProviderActivity.kt
└── res/layout/
    └── activity_dns_provider.xml
```

## Modified Files

- `app/build.gradle.kts` - Added Shizuku dependencies and AIDL support
- `app/src/main/AndroidManifest.xml` - Added permissions, service, and activity declarations
- `gradle/libs.versions.toml` - Updated AGP version

## Troubleshooting

### Tile doesn't appear
- Ensure the app is installed
- Restart your device
- Check that the service is declared in AndroidManifest.xml

### Permission errors
- Make sure Shizuku is running
- Grant permission to your app in Shizuku settings
- Restart Shizuku after device reboot

### DNS changes not taking effect
- Toggle WiFi off and on
- Restart your device
- Check Private DNS settings in Android Settings

### Shizuku connection drops
- Shizuku must be restarted after device reboot
- Open Shizuku app and tap "Start via Wireless debugging"

## Customization

### Add More DNS Providers

Edit the `dnsProviders` map in both `PrivateDNSTile.kt` and `DNSProviderActivity.kt`:

```kotlin
private val dnsProviders = mapOf(
    "AdGuard (Privacy)" to "dns.adguard.com",
    "Cloudflare (Fast)" to "1dot1dot1dot1.cloudflare-dns.com",
    "Google DNS" to "dns.google",
    "NextDNS (Security)" to "dns.nextdns.io",
    "Quad9 (Threat Protection)" to "dns.quad9.net",
    "LibreDNS (No Logs)" to "dot.libredns.gr",
    // Add your custom providers here
    "Mullvad DNS" to "dns.mullvad.net",
    "OpenDNS" to "dns.opendns.com"
)
```

## Resources

- [Shizuku Official Documentation](https://shizuku.rikka.app/)
- [Shizuku API GitHub](https://github.com/RikkaApps/Shizuku-API)
- [Android Quick Settings Tile Guide](https://developer.android.com/develop/ui/views/quicksettings-tiles)
