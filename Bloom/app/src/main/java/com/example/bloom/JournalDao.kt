package com.example.bloom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journals")
    fun getAllJournals(): Flow<List<Journal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: Journal)

    @Update
    suspend fun updateJournal(journal: Journal)

    @Delete
    suspend fun deleteJournal(journal: Journal)

    @Query("DELETE FROM journals")
    suspend fun deleteAllJournals()
}