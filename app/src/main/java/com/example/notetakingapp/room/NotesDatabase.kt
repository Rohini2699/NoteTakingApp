package com.example.notetakingapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import kotlin.concurrent.Volatile

@Database(
    entities = [Notes::class],
    version = 1,
    exportSchema = false,
)
abstract class NotesDatabase :RoomDatabase() {
    abstract val  db:NotesDao
    //val MIGRATION_1_2 = object : Migration(1, 2)
    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null
        fun getInstance(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "Notes_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

