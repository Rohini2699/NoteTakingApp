package com.example.notetakingapp.repository

import android.util.Log
import com.example.notetakingapp.room.Note
import com.example.notetakingapp.room.NotesDao

class NotesRepository (private val db:NotesDao) {

    val allnotes = db.getallnotes()
    val selectednotes = db.getSelectedNotes()

    suspend fun insertNote(notes: Note) {
        return db.insertnotes(notes)
    }
    suspend fun update(notes: Note) {


        return db.updatenotes(notes)

    }
    fun updateAll(isSelected:Int)
    {
        db.updateAll(isSelected)

    }
    suspend fun delete(notes: Note) {
        return db.deletenotes(notes)
    }
    suspend fun deselectall(){
         return db.deselectall()
     }
    suspend fun deleteNotesById(id:Int){
        return db.deleteNotesById(id)
    }

    fun searchNote(query:String?)=db.searchNote(query)
    // I am getting the selected notes.
    suspend fun savePinStatus( noteId: Int ,isPinned:Boolean ){

            db.updatePinStatus(noteId ,isPinned)
    }
//     suspend fun updateselectednotes(noteId: Int ,isSlected:Boolean):LiveData<List<Notes>>
//     {
//        return  db.updateselectednotes(noteId,isSlected)
//
//     }

    suspend fun saveImage(noteId: Int, imageArray: String) {
        db.saveImage(imageArray, noteId)
        Log.d("imagearray" ,"$imageArray")
    }

    suspend fun updateIsPinnedColumn(noteIds: List<Int>, isPinned: Int) {
        db.updateIsPinnedColumn(noteIds, isPinned)
    }


}