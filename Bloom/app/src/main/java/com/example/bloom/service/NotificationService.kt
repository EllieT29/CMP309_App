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
        val notification= NotificationCompat.Builder(context,"task_notification")
            .setContentTitle("Your Flower has bloomed!")
            .setContentText("All tasks complete - have a look at your beautiful flower!")
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
        val notification= NotificationCompat.Builder(context,"task_notification")
            .setContentTitle("Your New Task")
            .setContentText(taskTitle)
            .setSmallIcon(R.drawable.task_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        //Show the notification
        notificationManager.notify(Random.Default.nextInt(),notification)
    }
}