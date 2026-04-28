package com.example.bloom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }


        // AI was used for the database callback and populating the task db
        //The AI used was Google Gemini

        //DatabaseCallback handles actions to be performed when the database is created
        private class DatabaseCallback : Callback() {
            //Override onCreate to populate the database after it has been created
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                //Launch a coroutine on the IO thread to populate the tasks table
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.Companion.INSTANCE?.let { database ->
                        //Populate predefined tasks into the database using the TaskDao
                        populateTasks(database.taskDao())
                    }
                }
            }

            //Suspend function to insert predefined tasks into the database
            suspend fun populateTasks(taskDao: TaskDao) {
                //List of predefined tasks to insert into the database
                val predefinedTasks = listOf(
                    Task(title = "Go Outside", description = "Step into nature. Breathe in the fresh air.", isComplete = false),
                    Task(title = "Ground Yourself", description = "Place your hands on a tree, grass or earth and feel the connection.", isComplete = false),
                    Task(title = "Awaken the Senses", description = "Notice 3 colours, 3 sounds and 3 textures around you outside.", isComplete = false),
                    Task(title = "Release", description = "Take 3 deep breaths and softly let go of any tension.", isComplete = false),
                    Task(title = "Reflect", description = "In your journal, either your app journal or a physical one, write an insight or feeling about your time outside.", isComplete = false)
                )
                //Insert the predefined tasks into the database
                taskDao.insertAll(predefinedTasks)
            }
        }
    }
}
