package com.example.bloom

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(tasks: List<Task>) {
        taskDao.insertAll(tasks)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }
}