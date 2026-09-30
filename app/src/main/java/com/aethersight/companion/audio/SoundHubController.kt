package com.aethersight.companion.audio

import android.content.Context
import android.media.AudioManager
import android.util.Log

class SoundHubController(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun triggerAudioFeedback() {
        try {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
            Log.i("SoundHub", "Audio feedback triggered successfully via Sound Hub.")
        } catch (e: Exception) {
            Log.e("SoundHub", "Failed to trigger audio feedback", e)
        }
    }
}
