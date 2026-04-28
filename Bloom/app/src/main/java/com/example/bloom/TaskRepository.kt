package com.example.bloom

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "taskDate")

class TaskRepository(private val taskDao: TaskDao, private val context: Context) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    val completedTaskCount: Flow<Int> = taskDao.getCompletedTaskCount()

    val firstIncompleteTask: Flow<Task?> = taskDao.getFirstIncompleteTask()

    suspend fun insert(tasks: List<Task>) {
        taskDao.insertAll(tasks)
    }

    suspend fun resetTasks() {
        taskDao.resetAllTasks()
    }
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    private val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")

    val lastResetDate: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_RESET_DATE]
        }

    suspend fun saveLastResetDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_RESET_DATE] = date
        }
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }


     suspend fun checkAndResetTasks() {
            val lastResetDate = lastResetDate.first()
            val currentDate = getCurrentDate()

            if (lastResetDate != currentDate) {
                resetTasks()
                saveLastResetDate(currentDate)
            }
    }
}