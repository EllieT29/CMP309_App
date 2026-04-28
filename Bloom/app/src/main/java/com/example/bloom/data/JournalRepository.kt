package com.example.bloom.data

import kotlinx.coroutines.flow.Flow

//Repository for JournalDao
class JournalRepository(private val journalDao: JournalDao) {
    //Flow of all journals from the database
    val allJournals: Flow<List<Journal>> = journalDao.getAllJournals()

    //Suspend function for inserting a journal
    suspend fun insert(journal: Journal) {
        journalDao.insertJournal(journal)
    }

    //Suspend function for updating a journal
    suspend fun update(journal: Journal) {
        journalDao.updateJournal(journal)
    }

    //Suspend function for deleting a journal
    suspend fun delete(journal: Journal) {
        journalDao.deleteJournal(journal)
    }

    //Suspend function for deleting all journals
    suspend fun deleteAll() {
        journalDao.deleteAllJournals()
    }
}