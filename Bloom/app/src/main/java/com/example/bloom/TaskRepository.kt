package com.example.bloom

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Create a DataStore instance attached to Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "taskDate")

//Repository for tasks
class TaskRepository(private val taskDao: TaskDao, private val context: Context) {
    //Flow all tasks from task database
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    //Flow number of completed tasks from task database
    val completedTaskCount: Flow<Int> = taskDao.getCompletedTaskCount()

    //Flow first incomplete task from task database
    val firstIncompleteTask: Flow<Task?> = taskDao.getFirstIncompleteTask()

    //Reset all tasks to incomplete
    suspend fun resetTasks() {
        taskDao.resetAllTasks()
    }

    //Update task in task database
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    //Key used to store last reset date in DataStore
    private val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")

    //Flow last reset date from DataStore
    val lastResetDate: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_RESET_DATE]
        }

    //Save last reset date to DataStore
    suspend fun saveLastResetDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_RESET_DATE] = date
        }
    }

    //Get current date
    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    //Check if reset is required and reset tasks if needed
     suspend fun checkAndResetTasks() {
            val lastResetDate = lastResetDate.first()
            val currentDate = getCurrentDate()

            if (lastResetDate != currentDate) {
                resetTasks()
                saveLastResetDate(currentDate)
            }
    }
}