package com.example.notetakingapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.notetakingapp.room.Notes
import com.example.notetakingapp.room.NotesDao

class NotesRepository (private val db:NotesDao) {

    val allnotes = db.getallnotes()
    val selectednotes = db .getSelectedNotes()

    suspend fun insertNote(notes: Notes) {
        return db.insertnotes(notes)
    }
    suspend fun update(notes: Notes) {

        return db.updatenotes(notes)
    }
      suspend fun updateall():Int
    {
        val rowsUpdated = db.updateAll()
        Log.d("NoteRepository", "Number of notes updated: $rowsUpdated")
           return rowsUpdated
    }
    suspend fun delete(notes: Notes) {
        return db.deletenotes(notes)
    }
    suspend fun deselectall(){
         return db.deselectall()
     }
    suspend fun deleteNotesById(id:Int){
        return db.deleteNotesById(id)
    }

    fun searchNote(query:String?)=db.searchNote(query)
}