package com.aethersight.companion.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aethersight.companion.bluetooth.BleManager

class AetherService : Service() {

    private lateinit var bleManager: BleManager

    override fun onCreate() {
        super.onCreate()
        bleManager = BleManager(this)
        startForegroundService()
        bleManager.startScan()
    }

    private fun startForegroundService() {
        val channelId = "aether_sight_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Aether-Sight Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Aether-Sight Active")
            .setContentText("Monitoring Carbinox Blaze S telemetry...")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        bleManager.disconnect()
        super.onDestroy()
    }
}
