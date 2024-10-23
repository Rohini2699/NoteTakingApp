package com.note.notetakingapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlin.concurrent.Volatile

@Database(
    entities = [Note::class],
    version = 1,

)
@TypeConverters(PriorityConverter::class)
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

