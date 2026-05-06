package com.example.bloom.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.bloom.R
import kotlin.random.Random

//Notification service for showing notifications
class NotificationService (
    private val context: Context
){
    //Get the notification manager
    private val notificationManager=context.getSystemService(NotificationManager::class.java)

    //Show notification for task completion
    fun showTaskCompletedNotification(){
        //Create the notification
        val notification= NotificationCompat.Builder(context,context.getString(R.string.notification_channel_name))
            .setContentTitle(context.getString(R.string.completed_notification_title))
            .setContentText(context.getString(R.string.notification_description))
            .setSmallIcon(R.drawable.task_complete_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        //Show the notification
        notificationManager.notify(
            Random.Default.nextInt(),
            notification
        )
    }

    //Show notification for new task
    fun showNewTaskNotification(taskTitle:String){

        //Create the notification
        val notification= NotificationCompat.Builder(context,context.getString(R.string.notification_channel_name))
            .setContentTitle(context.getString(R.string.new_task_notification))
            .setContentText(taskTitle)
            .setSmallIcon(R.drawable.task_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        //Show the notification
        notificationManager.notify(Random.Default.nextInt(),notification)
    }
}