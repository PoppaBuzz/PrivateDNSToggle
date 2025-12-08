package com.jphat.privatednstoggle

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jphat.privatednstoggle.ui.theme.PrivateDNSToggleTheme
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
            PrivateDNSToggleTheme {
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
                val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=moe.shizuku.privileged.api"))
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "🔒 Private DNS Toggle",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Quick Settings Tile",
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
                Text(
                    text = "Setup Steps",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SetupStep(
                    number = "3",
                    title = "Add Widget or Tile",
                    description = "Long-press home screen → Widgets → Add widget, OR swipe down twice → Edit → Add 'Private DNS' tile"
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
                          "• First tap: Enable DNS\n" +
                          "• Second tap: Open provider selection",
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
                          "• LibreDNS - No logging",
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