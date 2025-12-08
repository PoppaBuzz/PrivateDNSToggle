# Testing Checklist - Verify Everything Works

## Why Nothing Happened When You Pressed the Tile

The Quick Settings Tile (and widget) **require Shizuku permission** to modify system DNS settings. Without it, they fail silently. Here's how to fix it:

## Step-by-Step Testing Guide

### 1. Install Shizuku (If Not Already Done)

```bash
# On your Android device:
1. Open Play Store
2. Search for "Shizuku"
3. Install the app by "Rikka"
```

### 2. Start Shizuku Service

```bash
# On your device:
1. Open Shizuku app
2. Tap "Start via Wireless debugging"
3. Follow the pairing instructions:
   - Enable Developer Options (Settings > About > Tap Build Number 7x)
   - Enable Wireless Debugging (Settings > Developer Options)
   - Pair the device in Shizuku
```

**Verify:** Shizuku app should show "Shizuku is running"

### 3. Rebuild and Install Your App

```bash
./gradlew clean
./gradlew installDebug
```

### 4. Grant Shizuku Permission

```bash
# On your device:
1. Open your "PrivateDNS Toggle" app
2. Tap "Request Permission" button
3. In the Shizuku dialog, tap "Allow"
4. You should see a toast: "✓ Shizuku permission granted!"
```

**Verify:** Toast message appears confirming permission

### 5. Test the Quick Settings Tile

```bash
# On your device:
1. Swipe down from top twice to open Quick Settings
2. Tap the edit button (pencil icon)
3. Find "Private DNS" tile
4. Drag it to your Quick Settings panel
5. Tap "Done"
6. Tap the "Private DNS" tile
```

**Expected behavior:**
- **First tap (when OFF):** Tile turns cyan/blue, subtitle shows "AdGuard" (or your default provider)
- **Second tap (when ON):** Opens provider selection screen
- **If permission missing:** Opens your app to request permission

### 6. Test the Home Screen Widget

```bash
# On your device:
1. Long-press on home screen
2. Tap "Widgets"
3. Find "PrivateDNS Toggle"
4. Drag to home screen
5. Tap "Toggle DNS" button
```

**Expected behavior:**
- **First tap:** Widget turns cyan, shows "ON - AdGuard"
- **Second tap:** Widget turns gray, shows "OFF"
- **If permission missing:** Opens your app to request permission

### 7. Verify DNS is Actually Working

```bash
# On your device:
1. Enable Private DNS (via tile or widget)
2. Go to Settings > Network & Internet > Private DNS
3. You should see "Private DNS provider hostname" selected
4. The hostname should match your provider (e.g., "dns.adguard.com")
```

**Or check via ADB:**
```bash
adb shell settings get global private_dns_mode
# Should return: hostname

adb shell settings get global private_dns_specifier
# Should return: dns.adguard.com (or your selected provider)
```

## Common Issues and Solutions

### Issue: Tile/Widget Does Nothing

**Cause:** Shizuku permission not granted

**Solution:**
1. Open your app
2. Tap "Request Permission"
3. Grant permission in Shizuku dialog
4. Try tile/widget again

### Issue: "Shizuku is not running" Toast

**Cause:** Shizuku service stopped

**Solution:**
1. Open Shizuku app
2. Tap "Start via Wireless debugging"
3. Try tile/widget again

### Issue: Tile Opens App Instead of Toggling

**Cause:** This is the NEW behavior when permission is missing

**Solution:**
- This is intentional! The app will open so you can grant permission
- After granting permission, the tile will work normally

### Issue: Widget Doesn't Update After Toggle

**Cause:** Widget needs manual refresh

**Solution:**
1. Remove widget from home screen
2. Re-add it
3. Try again

### Issue: Provider Selection Doesn't Work

**Cause:** Shizuku permission not granted

**Solution:**
1. Open provider selection
2. Tap a provider
3. If you see "Permission Error: Grant via Shizuku" toast
4. Open your app and grant permission

## Quick Verification Commands (ADB)

```bash
# Check if Shizuku is running
adb shell ps | grep shizuku

# Check current DNS mode
adb shell settings get global private_dns_mode

# Check current DNS provider
adb shell settings get global private_dns_specifier

# Manually enable DNS (for testing)
adb shell settings put global private_dns_mode hostname
adb shell settings put global private_dns_specifier dns.google

# Manually disable DNS (for testing)
adb shell settings put global private_dns_mode off
```

## Expected Behavior Summary

### Quick Settings Tile:
- ✅ **OFF → Tap → ON** (with saved provider)
- ✅ **ON → Tap → Opens provider selection**
- ✅ **No permission → Opens app**

### Home Screen Widget:
- ✅ **Tap "Toggle DNS" → Toggles ON/OFF**
- ✅ **Tap ⚙️ icon → Opens provider selection**
- ✅ **No permission → Opens app**
- ✅ **Background color changes** (gray = OFF, cyan = ON)
- ✅ **Status text updates** (shows provider when ON)

### Provider Selection:
- ✅ **Tap provider → Switches immediately**
- ✅ **Shows current selection** (radio button checked)
- ✅ **Tap "Disable" → Turns OFF**

## Final Verification

After completing all steps, you should be able to:

1. ✅ Toggle DNS on/off from Quick Settings tile
2. ✅ Toggle DNS on/off from home screen widget
3. ✅ Change DNS provider from either interface
4. ✅ See visual feedback (tile state, widget color)
5. ✅ Verify DNS is active in Android settings

## If Still Not Working

1. **Check Logcat for errors:**
   ```bash
   adb logcat | grep -i "privatednstoggle\|shizuku\|dns"
   ```

2. **Verify Shizuku permission in Shizuku app:**
   - Open Shizuku app
   - Tap "Authorized apps"
   - Your app should be listed

3. **Try manual ADB command:**
   ```bash
   adb shell settings put global private_dns_mode hostname
   adb shell settings put global private_dns_specifier dns.google
   ```
   If this fails, it's a device/Android version issue

4. **Restart everything:**
   - Restart Shizuku service
   - Force stop your app
   - Restart your device

---

**Bottom Line:** The tile and widget WILL work once Shizuku permission is granted. The improved error handling now opens your app automatically if permission is missing, making it much easier to troubleshoot!
