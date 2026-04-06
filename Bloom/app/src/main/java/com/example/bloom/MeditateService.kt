package com.example.bloom

import android.app.Service
import android.app.Service.START_STICKY
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder


//Used GeeksforGeeks - https://www.geeksforgeeks.org/kotlin/services-in-android-using-jetpack-compose/
class MeditateService : Service() {

    private lateinit var player: MediaPlayer

    //https://kotlinlang.org/docs/object-declarations.html#companion-objects
    companion object{
        var isPlaying: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if(!isPlaying){
            player = MediaPlayer.create(this, R.raw.meditate)
            player.setLooping(true)
            player.start()
            isPlaying = true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        player.stop()
        isPlaying = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}