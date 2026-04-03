package com.example.bloom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.text.insert


class JournalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JournalRepository
    val allJournals: Flow<List<Journal>>

    init {
        val journalDao = AppDatabase.getDatabase(application).journalDao()
        repository = JournalRepository(journalDao)
        allJournals = repository.allJournals
    }
    fun insert(journal: Journal) = viewModelScope.launch {
        repository.insert(journal)
    }

    fun update(journal: Journal) = viewModelScope.launch {
        repository.update(journal)
    }

    fun delete(journal: Journal) = viewModelScope.launch {
        repository.delete(journal)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}