package com.example.bloom

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    val completedTaskCount: Flow<Int> = taskDao.getCompletedTaskCount()

    val firstIncompleteTask: Flow<Task?> = taskDao.getFirstIncompleteTask()

    suspend fun insert(tasks: List<Task>) {
        taskDao.insertAll(tasks)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }
}