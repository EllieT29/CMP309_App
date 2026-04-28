package com.example.bloom

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity class for the Journal table
@Entity(tableName = "journals")
data class Journal(
    //Auto-generated primary key
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //Journal entry details
    val notes: String,
    val entryDate: Long
)