package com.jphat.quickdns

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.net.toUri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jphat.quickdns.ui.theme.QuickDNSTheme
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {
    
    private val requestPermissionCode = 1
    
    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == requestPermissionCode) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✓ Shizuku permission granted!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "✗ Shizuku permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Add permission result listener
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        
        // Check and request Shizuku permission
        checkAndRequestShizukuPermission()
        
        setContent {
            QuickDNSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SetupScreen(
                        modifier = Modifier.padding(innerPadding),
                        onRequestPermission = { checkAndRequestShizukuPermission() },
                        onOpenShizuku = { openShizukuApp() }
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
    }
    
    private fun checkAndRequestShizukuPermission() {
        if (!isShizukuInstalled()) {
            Toast.makeText(this, "Please install Shizuku app first", Toast.LENGTH_LONG).show()
            return
        }
        
        if (!Shizuku.pingBinder()) {
            Toast.makeText(this, "Shizuku is not running. Please start it.", Toast.LENGTH_LONG).show()
            return
        }
        
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "✓ Shizuku permission already granted!", Toast.LENGTH_SHORT).show()
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            Toast.makeText(this, "Permission needed to modify DNS settings", Toast.LENGTH_LONG).show()
            Shizuku.requestPermission(requestPermissionCode)
        } else {
            Shizuku.requestPermission(requestPermissionCode)
        }
    }
    
    private fun isShizukuInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    private fun openShizukuApp() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
            if (intent != null) {
                startActivity(intent)
            } else {
                // Try to open Play Store
                val playStoreIntent = Intent(Intent.ACTION_VIEW, "market://details?id=moe.shizuku.privileged.api".toUri())
                startActivity(playStoreIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Please install Shizuku from Play Store", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun SetupScreen(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit,
    onOpenShizuku: () -> Unit
) {
    var isSetupExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Image(
            painter = painterResource(id = R.drawable.quickdns_text),
            contentDescription = "QuickDNS Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(60.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Quick Settings Tile & Widgets",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Setup",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(onClick = { isSetupExpanded = !isSetupExpanded }) {
                        Text(if (isSetupExpanded) "Hide" else "Show")
                    }
                }
                
                if (isSetupExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Option 1: Shizuku (Recommended)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SetupStep(
                        number = "1",
                        title = "Install & Start Shizuku",
                        description = "Download from Play Store and start the service"
                    )
                    
                    Button(
                        onClick = onOpenShizuku,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Shizuku App")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SetupStep(
                        number = "2",
                        title = "Grant Permission",
                        description = "Allow this app to modify DNS settings"
                    )
                    
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Permission")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    HorizontalDivider()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Option 2: Manual ADB Setup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SetupStep(
                        number = "1",
                        title = "Enable USB Debugging",
                        description = "Settings → About Phone → Tap Build Number 7 times → Developer Options → USB Debugging"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SetupStep(
                        number = "2",
                        title = "Connect to Computer",
                        description = "Connect your device via USB cable"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SetupStep(
                        number = "3",
                        title = "Run ADB Command",
                        description = "Open terminal/command prompt and run:"
                    )
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "adb shell pm grant com.jphat.quickdns android.permission.WRITE_SECURE_SETTINGS",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "How to Use",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Widget (Home Screen):\n" +
                          "• Tap 'Toggle DNS' to turn on/off\n" +
                          "• Tap settings icon to change provider\n\n" +
                          "Quick Settings Tile:\n" +
                          "• Tap to toggle DNS on/off\n" +
                          "• When DNS is on, tap again to disable or change provider",
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "DNS Providers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "• AdGuard - Privacy & ad blocking\n" +
                          "• Cloudflare - Fast performance\n" +
                          "• Google DNS - Reliable\n" +
                          "• NextDNS - Security focused\n" +
                          "• Quad9 - Threat protection\n" +
                          "• DNS0 - Privacy focused\n" +
                          "• DNS.SB - No logs\n" +
                          "• LibreDNS - No logging\n" +
                          "• Mullvad - Privacy focused\n" +
                          "• OpenDNS - Cisco operated\n" +
                          "• CleanBrowsing - Family & Security filters\n" +
                          "• Control D - Customizable\n\n" +
                          "You can also add your own custom DNS provider.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SetupStep(number: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
