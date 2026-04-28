package com.example.bloom.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.bloom.R

//Used GeeksforGeeks - https://www.geeksforgeeks.org/kotlin/services-in-android-using-jetpack-compose/
//Used Medium for enhancing the service (binding, pause feature etc -
//https://medium.com/@anna972606/bound-service-in-android-72dbf2069f49
//Service for handling meditation track and functionality
class MeditateService : Service() {

    //MediaPlayer for playing the meditation track
    private var player: MediaPlayer? = null

    //Binder for binding the service
    private var binder = LocalBinder()

    //Inner class for binding the service
    inner class LocalBinder : Binder() {
        fun getService(): MeditateService = this@MeditateService
    }

    //Called when client binds to the service
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    //Check if the service is playing
    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    //Check if the service has started
    fun isStarted(): Boolean{
        return player != null
    }

    //Play the meditation track
    fun play() {
        try {
            if (player == null) {//If player is null, create a new MediaPlayer
                player = MediaPlayer.create(this, R.raw.meditate)
                player?.isLooping = true
            }
            //Start playing the track
            if (player != null && !player!!.isPlaying) {
                player?.start()
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Pause the meditation track
    fun pause() {
        if (player != null && player?.isPlaying == true) {
            player?.pause()
        }
    }

    //Stop the meditation track
    fun stop() {
        if (player != null && player?.isPlaying == true) {
            player?.stop()
            player?.release()
            player = null
        }
    }

    //Destroy the service
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

}