package com.example.bloom

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder


//Used GeeksforGeeks - https://www.geeksforgeeks.org/kotlin/services-in-android-using-jetpack-compose/
class MeditateService : Service() {

    private var player: MediaPlayer? = null

    private var binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MeditateService = this@MeditateService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun play() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.meditate)
            player?.isLooping = true
        }
        if (player != null && !player!!.isPlaying) {
            player?.start()
        }
    }

    fun pause() {
        if (player != null && player?.isPlaying == true) {
            player?.pause()
        }
    }

    fun stop() {
        if (player != null && player?.isPlaying == true) {
            player?.stop()
            player?.release()
            player = null
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

}