package com.example.bloom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Data access object for tasks
@Dao
interface TaskDao {
    //Query for getting all tasks
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    //Query for inserting all tasks
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertAll(tasks: List<Task>)

    //Query for updating a task
    @Update
    suspend fun updateTask(task: Task)

    //Query for getting the number of completed tasks
    @Query("SELECT COUNT(*) FROM tasks WHERE isComplete = 1")
    fun getCompletedTaskCount(): Flow<Int>

    //Query for getting the first incomplete task
    @Query("SELECT * FROM tasks WHERE isComplete = 0 LIMIT 1")
    fun getFirstIncompleteTask(): Flow<Task?>

    //Query for resetting all tasks to incomplete
    @Query("UPDATE tasks SET isComplete = 0")
    suspend fun resetAllTasks()
}