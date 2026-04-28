package com.example.bloom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//ViewModel for tasks
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    //Repository for tasks
    private val repository: TaskRepository
    //Flow all tasks from task database
    val allTasks: Flow<List<Task>>

    //Initialise the repository, get all tasks and check if reset is required
    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao, application)
        allTasks = repository.allTasks
        viewModelScope.launch {repository.checkAndResetTasks()}
    }

    //Update task in task database
    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    //Get number of completed tasks from task database
    val completedTaskCount: Flow<Int> = repository.completedTaskCount

    //Get first incomplete task from task database
    val firstIncompleteTask: Flow<Task?> = repository.firstIncompleteTask

}