package com.example.tutor

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TutorUser::class, TutorProfile::class], version = 2, exportSchema = false)
abstract class TutorDatabase : RoomDatabase() {
    abstract fun tutorDao(): TutorDao
    abstract fun tutorProfileDao(): TutorProfileDao

    companion object {
        @Volatile
        private var INSTANCE: TutorDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TutorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TutorDatabase::class.java,
                    "anna_tutor.db"
                )
                    .addCallback(TutorDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class TutorDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(database: TutorDatabase) {
                val userDao = database.tutorDao()
                if (userDao.getUserCount() == 0) {
                    userDao.insertUsers(TutorPreloadData.initialUsers)
                }

                val profileDao = database.tutorProfileDao()
                if (profileDao.getTutorCount() == 0) {
                    profileDao.insertTutors(TutorPreloadData.initialTutorProfiles)
                }
            }
        }
    }
}
