package com.aethersight.companion

import android.bluetooth.*
import android.content.Context
import android.media.RingtoneManager
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.util.Log
import java.util.*

class BluetoothManager(private val context: Context) {

    companion object {
        private const val TAG = "BluetoothManager"
        
        // Command mappings for Carbinox Blaze S wrist gestures
        private const val CMD_PLAY_PAUSE = "f101"
        private const val CMD_NEXT_TRACK = "f102"
        private const val CMD_PREV_TRACK = "f103"
        private const val CMD_FIND_PHONE = "e999"
    }

    private var bluetoothAdapter: BluetoothAdapter? = null

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    fun startScanning() {
        Log.d(TAG, "Initiating BLE Discovery for Carbinox Blaze S...")
    }

    fun handleIncomingCommand(hexPayload: String) {
        Log.d(TAG, "Processed watch telemetry payload: $hexPayload")

        when (hexPayload) {
            CMD_PLAY_PAUSE -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            CMD_NEXT_TRACK -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            CMD_PREV_TRACK -> dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            CMD_FIND_PHONE -> triggerFindMyPhoneHub()
            else -> {
                Log.w(TAG, "Unrecognized command signature: $hexPayload")
            }
        }
    }

    private fun dispatchMediaKey(keyCode: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        
        val downEvent = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, keyCode, 0)
        val upEvent = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyCode, 0)
        
        try {
            audioManager.dispatchMediaKeyEvent(downEvent)
            audioManager.dispatchMediaKeyEvent(upEvent)
            Log.d(TAG, "Successfully dispatched media key: $keyCode")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to dispatch media key event", e)
        }
    }

    private fun triggerFindMyPhoneHub() {
        Log.i(TAG, "Sonic Hub Find My Phone alarm initiated from wrist gesture!")
        
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) 
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            val ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone.play()

            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                val timings = longArrayOf(0, 500, 200, 500, 200, 1000)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing Find My Phone sound hub activation", e)
        }
    }
}
