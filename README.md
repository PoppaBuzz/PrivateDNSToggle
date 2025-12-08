# Private DNS Toggle

An Android app that provides quick and easy control over Private DNS (DNS-over-TLS) settings with widgets, Quick Settings tile, and custom DNS provider management.

## Features

- **Quick Settings Tile** - Toggle Private DNS on/off from your notification shade
- **Home Screen Widgets** - Two widget sizes (1x1 compact and 2x1 with button)
- **10 Pre-configured DNS Providers** including Cloudflare, Google, AdGuard, Quad9, and more
- **Custom DNS Providers** - Add your own DNS-over-TLS providers as you discover them
- **One-Tap Toggle** - Quickly enable/disable Private DNS
- **Provider Selection** - Easily switch between different DNS providers
- **Persistent Settings** - Your selected provider is remembered across reboots

## Requirements

- Android 7.0 (API 24) or higher
- **Shizuku** - Required for modifying system settings without root

## Installation

### Option 1: Using Shizuku (Recommended)

1. Install [Shizuku](https://shizuku.rikka.app/) from Google Play or GitHub
2. Start Shizuku service (via wireless debugging or root)
3. Install Private DNS Toggle
4. Grant Shizuku permission when prompted

### Option 2: Using ADB (For Tech-Savvy Users)

If you prefer not to use Shizuku, you can grant the required permission directly via ADB:

1. Enable Developer Options on your Android device
2. Enable USB Debugging in Developer Options
3. Connect your device to your computer
4. Install the app on your device
5. Run the following ADB command:

```bash
adb shell pm grant com.jphat.privatednstoggle android.permission.WRITE_SECURE_SETTINGS
```

**Note:** This permission will persist until you uninstall the app. You only need to run this command once after installation.

## Usage

### Quick Settings Tile

1. Add "Private DNS" tile to your Quick Settings
2. Tap once when OFF to enable with your saved provider
3. Tap when ON to open provider selection

### Widgets

**1x1 Compact Widget:**
- Tap anywhere to toggle DNS on/off
- Tap wrench icon to open provider selection

**2x1 Large Widget:**
- Tap "Toggle" button to switch DNS on/off
- Tap wrench icon to open provider selection

### Adding Custom DNS Providers

1. Open the app or tap the wrench icon on a widget
2. Tap "Add Provider" button
3. Enter provider name (e.g., "Comodo Secure DNS")
4. Enter DNS hostname (e.g., "dns.comodo.com")
5. Tap "Add"
6. Your custom provider appears in the list with a delete button

Custom providers sync across all widgets and the Quick Settings tile.

## Pre-configured DNS Providers

- **AdGuard** - Privacy-focused with ad blocking
- **Cloudflare** - Fast and privacy-respecting (1.1.1.1)
- **Google DNS** - Reliable and fast
- **NextDNS** - Security and privacy with customization
- **Quad9** - Threat protection and malware blocking
- **LibreDNS** - No logging, privacy-focused
- **Mullvad** - Privacy-focused from VPN provider
- **OpenDNS** - Cisco's DNS service
- **CleanBrowsing** - Family-friendly filtering
- **Control D** - Customizable DNS service

## Permissions

- `WRITE_SECURE_SETTINGS` - Required to modify Private DNS settings (granted via Shizuku)
- `INTERACT_ACROSS_USERS_FULL` - Required for Shizuku provider

## Technical Details

- **Language:** Kotlin
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Dependencies:**
  - Shizuku API for system settings access
  - AndroidX AppCompat
  - Jetpack Compose (for main activity)

## Building

```bash
git clone https://github.com/PoppaBuzz/privatednstoggle.git
cd privatednstoggle
./gradlew assembleDebug
```

## How It Works

The app requires the `WRITE_SECURE_SETTINGS` permission to modify `Settings.Global` values:
- `private_dns_mode` - Controls DNS state (off/hostname/automatic)
- `private_dns_specifier` - Sets the DNS-over-TLS hostname

This permission can be granted either through Shizuku (user-friendly) or directly via ADB (one-time setup). Neither method requires root access.

### Manual DNS Control via ADB

You can also control Private DNS directly from the command line:

```bash
# Enable Private DNS with a specific provider
adb shell settings put global private_dns_mode hostname
adb shell settings put global private_dns_specifier dns.google

# Disable Private DNS
adb shell settings put global private_dns_mode off

# Check current DNS mode
adb shell settings get global private_dns_mode

# Check current DNS provider
adb shell settings get global private_dns_specifier
```

## Troubleshooting

**"Permission Error: Grant via Shizuku"**
- Make sure Shizuku is running
- Grant Shizuku permission in the app when prompted
- Restart the app if needed
- Alternatively, grant permission via ADB (see Installation section)

**ADB permission not working**
- Verify the command ran successfully (no error output)
- Check that USB debugging is enabled
- Try revoking and re-granting: 
  ```bash
  adb shell pm revoke com.jphat.privatednstoggle android.permission.WRITE_SECURE_SETTINGS
  adb shell pm grant com.jphat.privatednstoggle android.permission.WRITE_SECURE_SETTINGS
  ```

**Widget not updating**
- Tap the widget to refresh
- Check that Shizuku is still running

**Custom provider not working**
- Verify the hostname is correct (should be DNS-over-TLS hostname)
- Check your internet connection
- Some providers may not work on all networks

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- [Shizuku](https://github.com/RikkaApps/Shizuku) by RikkaApps
- DNS provider information from various public DNS services

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
