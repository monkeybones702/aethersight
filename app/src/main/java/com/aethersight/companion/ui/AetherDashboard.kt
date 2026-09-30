package com.aethersight.companion.ui

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
import com.aethersight.companion.bluetooth.BleManager
import com.aethersight.companion.audio.SoundHubController

@Composable
fun AetherDashboard(bleManager: BleManager, soundController: SoundHubController) {
    val connectionState by bleManager.connectionState.collectAsState()
    val deviceData by bleManager.deviceData.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "AETHER-SIGHT",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Blaze S Gesture & Sound Hub",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Connection Status", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = connectionState, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Telemetry Data", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = deviceData, fontSize = 13.sp)
                }
            }

            // Action Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { bleManager.startScan() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = "Scan & Connect Blaze S", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { soundController.triggerAudioFeedback() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = "Trigger Sound Hub", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { bleManager.disconnect() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text(text = "Disconnect", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
