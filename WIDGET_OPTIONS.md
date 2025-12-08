# Widget Options & DNS Providers Guide

## ✅ Two Widget Sizes Available!

Your app now includes **two widget options** to fit your home screen layout:

### 1. Compact Widget (1x1) 
**Best for:** Minimal space, quick toggle

**Features:**
- Smallest possible size (1x1 grid cell)
- Tap anywhere to toggle DNS on/off
- Tiny settings icon in corner
- Shows status: "OFF" or provider name
- Color-coded: Gray (OFF) / Cyan (ON)

**How to use:**
- Tap widget → Toggle DNS
- Tap ⚙️ icon → Change provider

---

### 2. Large Widget (2x1) ⭐ NEW!
**Best for:** Clear controls, dedicated button

**Features:**
- Horizontal layout (2x1 grid cells)
- Dedicated "Toggle" button
- Larger settings button
- Shows full status text
- Color-coded background

**How to use:**
- Tap "Toggle" button → Toggle DNS
- Tap ⚙️ icon → Change provider

---

## 10 DNS Providers Included!

All widgets, tile, and provider selection now include **10 DNS providers**:

### Privacy-Focused:
1. **AdGuard** - `dns.adguard.com`
   - Blocks ads, trackers, and malware
   - Privacy-focused, no logging

2. **Mullvad** - `dns.mullvad.net`
   - From Mullvad VPN
   - Maximum privacy, no logging
   - No filtering

3. **LibreDNS** - `dot.libredns.gr`
   - Open-source, no logging
   - Ad blocking available

### Performance:
4. **Cloudflare** - `1dot1dot1dot1.cloudflare-dns.com`
   - Fastest DNS resolver
   - Privacy-focused (1.1.1.1)
   - Minimal logging

5. **Google DNS** - `dns.google`
   - Reliable and fast
   - Global infrastructure
   - Some logging for diagnostics

### Security:
6. **NextDNS** - `dns.nextdns.io`
   - Advanced threat protection
   - Customizable filtering
   - Parental controls

7. **Quad9** - `dns.quad9.net`
   - Blocks malicious domains
   - Threat intelligence
   - Privacy-focused

8. **CleanBrowsing** - `security-filter-dns.cleanbrowsing.org`
   - Family-friendly filtering
   - Blocks adult content
   - Malware protection

### General Purpose:
9. **OpenDNS** - `dns.opendns.com`
   - By Cisco
   - Phishing protection
   - Customizable filtering

10. **Control D** - `freedns.controld.com`
    - Highly customizable
    - Multiple filter options
    - Free tier available

---

## How to Add Widgets

### Add Compact Widget (1x1):
1. Long-press home screen
2. Tap "Widgets"
3. Find "Private DNS (1x1)"
4. Drag to home screen
5. Place in 1x1 space

### Add Large Widget (2x1):
1. Long-press home screen
2. Tap "Widgets"
3. Find "Private DNS (2x1)"
4. Drag to home screen
5. Place in 2x1 space (horizontal)

**Note:** You can add both widgets to your home screen if you want!

---

## Widget Comparison

| Feature | Compact (1x1) | Large (2x1) |
|---------|---------------|-------------|
| Size | 40x40 dp | 110x40 dp |
| Toggle | Tap anywhere | "Toggle" button |
| Settings | Tiny ⚙️ icon | Large ⚙️ button |
| Status | Small text | Full text |
| Best for | Minimal space | Clear controls |

---

## Adding Custom DNS Providers

Want to add your own DNS provider? Edit these files:

### 1. Update Provider Lists

Edit the `dnsProviders` map in:
- `DNSProviderActivity.kt`
- `PrivateDNSTile.kt`
- `PrivateDNSWidget.kt`
- `PrivateDNSWidgetLarge.kt`

**Example:**
```kotlin
private val dnsProviders = mapOf(
    "AdGuard (Privacy)" to "dns.adguard.com",
    // ... existing providers ...
    "Your Provider" to "dns.yourprovider.com"
)
```

### 2. Rebuild and Install
```bash
./gradlew installDebug
```

### Popular DNS Providers to Add:

**Alternate DNS:**
```kotlin
"Alternate DNS" to "dns.alternate-dns.com"
```

**Comodo Secure DNS:**
```kotlin
"Comodo" to "dns.comodo.com"
```

**DNS.Watch:**
```kotlin
"DNS.Watch" to "dns.watch"
```

**Yandex DNS:**
```kotlin
"Yandex" to "common.dot.dns.yandex.net"
```

**Cisco Umbrella:**
```kotlin
"Umbrella" to "dns.umbrella.com"
```

---

## Provider Selection Screen

When you tap the settings icon (⚙️), you'll see:

- **List of all 10 providers** with descriptions
- **Radio button** showing current selection
- **Tap any provider** to switch immediately
- **"Disable Private DNS" button** at bottom

Changes apply instantly - no need to restart!

---

## Quick Reference

### Compact Widget (1x1):
```
┌──────┐
│ DNS  │
│ OFF  │
│   ⚙️ │
└──────┘
```

### Large Widget (2x1):
```
┌─────────────────────────┐
│ Private DNS  [Toggle] ⚙️│
│ OFF                     │
└─────────────────────────┘
```

---

## Tips

1. **Use both widgets** - Compact on main screen, large on secondary screen
2. **Try different providers** - Some are faster in different regions
3. **Check compatibility** - Some apps may not work with certain DNS providers
4. **Restart after changes** - Toggle WiFi if DNS doesn't update immediately
5. **Monitor performance** - Use speed test apps to compare providers

---

## Troubleshooting

**Widget shows wrong provider:**
- Remove and re-add widget
- Toggle DNS off and on

**Can't see all 10 providers:**
- Rebuild app: `./gradlew installDebug`
- Clear app data and re-grant Shizuku permission

**Widget doesn't fit:**
- Compact: Needs 1x1 space minimum
- Large: Needs 2x1 space minimum (horizontal)
- Both are resizable to larger sizes

---

## Next Steps

1. Rebuild: `./gradlew installDebug`
2. Add your preferred widget size
3. Try different DNS providers
4. Enjoy fast, private DNS!
