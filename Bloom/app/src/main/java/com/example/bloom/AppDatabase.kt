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
    abstract fun journalDao(): JournalDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
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
        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.Companion.INSTANCE?.let { database ->
                        populateTasks(database.taskDao())
                    }
                }
            }

            suspend fun populateTasks(taskDao: TaskDao) {
                val predefinedTasks = listOf(
                    Task(title = "Go Outside", description = "Step into nature. Breathe in the fresh air.", isComplete = false),
                    Task(title = "Ground Yourself", description = "Place your hands on a tree, grass or earth and feel the connection.", isComplete = false),
                    Task(title = "Awaken the Senses", description = "Notice 3 colours, 3 sounds and 3 textures around you outside.", isComplete = false),
                    Task(title = "Release", description = "Take 3 deep breaths and softly let go of any tension.", isComplete = false),
                    Task(title = "Reflect", description = "In your journal, either your app journal or a physical one, write an insight or feeling about your time outside.", isComplete = false)
                )
                taskDao.insertAll(predefinedTasks)
            }
        }
    }
}
