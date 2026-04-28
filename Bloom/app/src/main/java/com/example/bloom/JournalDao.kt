package com.example.bloom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Data Access Object for Room Database
@Dao
interface JournalDao {
    //Query to fetch all rows from the journals table
    @Query("SELECT * FROM journals")
    fun getAllJournals(): Flow<List<Journal>>

    //Query to insert a journal into the journals table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: Journal)

    //Query to update a journal in the journals table
    @Update
    suspend fun updateJournal(journal: Journal)

    //Query to delete a journal from the journals table
    @Delete
    suspend fun deleteJournal(journal: Journal)

    //Query to delete all journals from the journals table
    @Query("DELETE FROM journals")
    suspend fun deleteAllJournals()
}