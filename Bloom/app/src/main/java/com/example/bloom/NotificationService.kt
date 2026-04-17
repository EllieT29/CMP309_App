package com.example.bloom

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationService (
    private val context: Context
){
    private val notificationManager=context.getSystemService(NotificationManager::class.java)

    fun showTaskCompletedNotification(){
        val notification= NotificationCompat.Builder(context,"task_notification")
            .setContentTitle("Your Flower has bloomed!")
            .setContentText("All tasks complete - have a look at your beautiful flower!")
            .setSmallIcon(R.drawable.task_complete_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showNewTaskNotification(taskTitle:String){
        val notification=NotificationCompat.Builder(context,"task_notification")
            .setContentTitle("Your New Task")
            .setContentText("$taskTitle")
            .setSmallIcon(R.drawable.task_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(),notification)
    }
}