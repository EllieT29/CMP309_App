package com.example.bloom

import kotlinx.coroutines.flow.Flow


class JournalRepository(private val journalDao: JournalDao) {
    val allJournals: Flow<List<Journal>> = journalDao.getAllJournals()

    suspend fun insert(journal: Journal) {
        journalDao.insertJournal(journal)
    }

    suspend fun update(journal: Journal) {
        journalDao.updateJournal(journal)
    }

    suspend fun delete(journal: Journal) {
        journalDao.deleteJournal(journal)
    }

    suspend fun deleteAll() {
        journalDao.deleteAllJournals()
    }
}