package com.aethersight.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aethersight.companion.bluetooth.BleManager
import com.aethersight.companion.audio.SoundHubController
import com.aethersight.companion.ui.AetherDashboard
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    private lateinit var bleManager: BleManager
    private lateinit var soundController: SoundHubController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        bleManager = BleManager(applicationContext)
        soundController = SoundHubController(applicationContext)

        setContent {
            MaterialTheme {
                AetherDashboard(bleManager = bleManager, soundController = soundController)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager.disconnect()
    }
}
