package com.example.notetakingapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.concurrent.Volatile



@Database(entities = [Notes::class], version = 2 , exportSchema = false)

abstract class NotesDatabase :RoomDatabase() {
    abstract val  db:NotesDao
    //val MIGRATION_1_2 = object : Migration(1, 2)

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null
        fun getInstance(context: Context): NotesDatabase {
            synchronized(this) {


                var instance = INSTANCE
                if (instance == null) {
                    // creating the database objects
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotesDatabase::class.java,
                        "Notes_db"
                    )
                        .build()
                }
                INSTANCE = instance
                return instance


            }

        }
    }
}



