package com.aethersight.companion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aethersight.companion.bluetooth.BleManager
import com.aethersight.companion.service.AetherService
import com.aethersight.companion.ui.DashboardScreen

class MainActivity : ComponentActivity() {

    private lateinit var bleManager: BleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()
        bleManager = BleManager(this)

        val serviceIntent = Intent(this, AetherService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        setContent {
            DashboardScreen(bleManager = bleManager)
        }
    }
}
