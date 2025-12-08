# Private DNS Home Screen Widget Guide

## ✅ Widget Implementation Complete!

Your app now includes a **home screen widget** that lets you toggle Private DNS directly from your home screen.

## What You Get

### Home Screen Widget Features:
- **One-tap toggle** - Turn Private DNS on/off instantly
- **Visual status** - Shows "ON - [Provider]" or "OFF"
- **Color-coded** - Cyan when active, gray when inactive
- **Settings button** - Quick access to change DNS provider
- **Auto-updates** - Widget refreshes to show current state

### You Still Have:
- Quick Settings Tile (swipe down from top)
- Provider selection activity (6 DNS providers)
- Full Shizuku integration

## How to Add the Widget to Your Home Screen

1. **Build and install the updated app:**
   ```bash
   ./gradlew installDebug
   ```

2. **Long-press on your home screen**
   - Tap "Widgets" from the menu

3. **Find "PrivateDNS Toggle"**
   - Scroll through the widget list
   - Look for your app name

4. **Drag the widget to your home screen**
   - Choose a location
   - Release to place it

5. **Done!** The widget is ready to use

## How to Use the Widget

### Toggle DNS On/Off
- **Tap the "Toggle DNS" button** on the widget
- Widget turns **cyan** when DNS is ON
- Widget turns **gray** when DNS is OFF
- Status text shows current provider when ON

### Change DNS Provider
- **Tap the settings icon** (⚙️) in the top-right of the widget
- Opens the provider selection screen
- Choose from 6 providers:
  - AdGuard (Privacy)
  - Cloudflare (Fast)
  - Google DNS
  - NextDNS (Security)
  - Quad9 (Threat Protection)
  - LibreDNS (No Logs)
- Widget automatically updates to show new provider

## Widget Appearance

### When OFF:
```
┌─────────────────────────┐
│ Private DNS        ⚙️   │
│ OFF                     │
│                         │
│   [  Toggle DNS  ]      │
└─────────────────────────┘
Gray background
```

### When ON:
```
┌─────────────────────────┐
│ Private DNS        ⚙️   │
│ ON - Cloudflare         │
│                         │
│   [  Toggle DNS  ]      │
└─────────────────────────┘
Cyan background
```

## Three Ways to Control Private DNS

Now you have **three options** to control Private DNS:

1. **Home Screen Widget** ⭐ NEW!
   - Always visible on home screen
   - One-tap toggle
   - Quick settings access

2. **Quick Settings Tile**
   - Swipe down from top
   - Tap to toggle
   - Tap again to change provider

3. **Open the App**
   - Full setup instructions
   - Permission management
   - Provider information

## Widget Size

- **Minimum size:** 2x2 grid cells (180dp x 110dp)
- **Resizable:** Yes, can be made larger
- **Recommended:** 2x2 or 3x2 for best appearance

## Troubleshooting

### Widget doesn't appear in widget list
- Make sure the app is installed
- Restart your device
- Check that the receiver is declared in AndroidManifest.xml

### Widget doesn't update after toggle
- Grant Shizuku permission first
- Make sure Shizuku is running
- Try removing and re-adding the widget

### Toggle button doesn't work
- Verify Shizuku permission is granted
- Check that Shizuku service is running
- Open the app and tap "Request Permission"

### Settings button opens wrong screen
- This is expected - it opens the provider selection
- Choose a provider or tap "Disable Private DNS"

## Customization Ideas

Want to customize the widget? Edit these files:

- **Layout:** `app/src/main/res/layout/widget_private_dns.xml`
- **Colors:** `widget_background_active.xml` and `widget_background_inactive.xml`
- **Size:** `app/src/main/res/xml/widget_info.xml`
- **Logic:** `app/src/main/java/com/jphat/privatednstoggle/widget/PrivateDNSWidget.kt`

## Files Created

```
app/src/main/
├── java/com/jphat/privatednstoggle/widget/
│   └── PrivateDNSWidget.kt (Widget provider)
├── res/
│   ├── layout/
│   │   └── widget_private_dns.xml (Widget layout)
│   ├── xml/
│   │   └── widget_info.xml (Widget metadata)
│   └── drawable/
│       ├── widget_background_active.xml (Cyan background)
│       └── widget_background_inactive.xml (Gray background)
```

## Next Steps

1. Rebuild and install the app
2. Add the widget to your home screen
3. Tap to toggle DNS on/off
4. Tap settings icon to change providers
5. Enjoy quick access to Private DNS!

---

**Note:** The widget uses the same Shizuku permissions as the Quick Settings tile, so if you've already granted permission, the widget will work immediately!
