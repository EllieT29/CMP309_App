package com.example.bloom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.bloom.R

@Database(entities = [Journal::class, Task::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    //Define abstract methods for accessing the DAOs
    abstract fun journalDao(): JournalDao
    abstract fun taskDao(): TaskDao

    //Companion object for managing the singleton instance of the database
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //Get the singleton instance of the database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                //If INSTANCE is null, create the database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }


        // AI was used for the database callback and populating the task db
        //The AI used was Google Gemini

        //DatabaseCallback handles actions to be performed when the database is created
        private class DatabaseCallback(private val context: Context) : Callback() {
            //Override onCreate to populate the database after it has been created
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                //Launch a coroutine on the IO thread to populate the tasks table
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        //Populate predefined tasks into the database using the TaskDao
                        populateTasks(database.taskDao(), context)
                    }
                }
            }

            //Suspend function to insert predefined tasks into the database
            suspend fun populateTasks(taskDao: TaskDao, context: Context) {
                //List of predefined tasks to insert into the database
                val predefinedTasks = listOf(
                    Task(
                        title = context.getString(R.string.task1_title),
                        description = context.getString(R.string.task1_description),
                        isComplete = false
                    ),
                    Task(
                        title = context.getString(R.string.task2_title),
                        description = context.getString(R.string.task2_description),
                        isComplete = false
                    ),
                    Task(
                        title = context.getString(R.string.task3_title),
                        description = context.getString(R.string.task3_description),
                        isComplete = false
                    ),
                    Task(
                        title = context.getString(R.string.task4_title),
                        description = context.getString(R.string.task4_description),
                        isComplete = false
                    ),
                    Task(
                        title = context.getString(R.string.task5_title),
                        description = context.getString(R.string.task5_description),
                        isComplete = false
                    )
                )
                //Insert the predefined tasks into the database
                taskDao.insertAll(predefinedTasks)
            }
        }
    }
}