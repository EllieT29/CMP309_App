package com.example.bloom

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity for tasks
@Entity(tableName = "tasks")
data class Task(
    //Primary key for tasks
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //Values for tasks
    val title: String,
    val description: String,
    val isComplete: Boolean
)