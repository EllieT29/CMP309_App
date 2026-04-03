package com.example.bloom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journals")
data class Journal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notes: String,
    val entryDate: Long
)