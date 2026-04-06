package com.example.bloom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: Flow<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }
    val completedTaskCount: Flow<Int> = repository.completedTaskCount

    val firstIncompleteTask: Flow<Task?> = repository.firstIncompleteTask

}