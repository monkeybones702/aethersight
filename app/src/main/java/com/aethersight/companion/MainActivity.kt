package com.aethersight.companion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.aethersight.companion.ui.theme.AetherSightTheme

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    
    // Observable connection state for the UI
    var connectionStatus = mutableStateOf("Disconnected")
        private set

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permissions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        bluetoothManager = BluetoothManager(this)
        checkAndRequestPermissions()

        setContent {
            AetherSightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AetherDashboard(
                        statusText = connectionStatus.value,
                        onConnectClick = {
                            connectionStatus.value = "Scanning for Blaze S..."
                            bluetoothManager.startScanning()
                            // Simulate connection handshake response for testing UI feedback
                            window.decorView.postDelayed({
                                connectionStatus.value = "Connected: Carbinox Blaze S"
                            }, 2000)
                        },
                        onTestAlarmClick = { 
                            bluetoothManager.handleIncomingCommand("e999") 
                        }
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            requestPermissionLauncher.launch(needed.toTypedArray())
        }
    }
}

@Composable
fun AetherDashboard(
    statusText: String,
    onConnectClick: () -> Unit,
    onTestAlarmClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AETHER-SIGHT",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Carbinox Blaze S Bridge",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Status: $statusText", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Target: Carbinox Blaze S (BLE)", fontSize = 12.sp, color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConnectClick,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Connect to Blaze S", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onTestAlarmClick,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Test Find My Phone Alert", fontSize = 16.sp)
        }
    }
}
