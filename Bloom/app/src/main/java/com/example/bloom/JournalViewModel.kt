package com.example.bloom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


//ViewModel for JournalDao
class JournalViewModel(application: Application) : AndroidViewModel(application) {
    //Initialising journal repository
    private val repository: JournalRepository
    //Flow of all journals from the database
    val allJournals: Flow<List<Journal>>

    //Initialising the repository and all journals
    init {
        val journalDao = AppDatabase.getDatabase(application).journalDao()
        repository = JournalRepository(journalDao)
        allJournals = repository.allJournals
    }

    //Functions for inserting, updating, deleting and deleting all journals
    fun insert(journal: Journal) = viewModelScope.launch {
        repository.insert(journal)
    }

    fun delete(journal: Journal) = viewModelScope.launch {
        repository.delete(journal)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}