package com.note.notetakingapp.repository

import com.note.notetakingapp.room.Note
import com.note.notetakingapp.room.NotesDao
import com.note.notetakingapp.room.Priority

class NotesRepository(private val db: NotesDao) {

    val allnotes = db.getAllNotes()

    suspend fun insertNote(notes: Note) {
        return db.insertNote(notes)
    }

    suspend fun update(notes: Note) {
        return db.updateNote(notes)
    }

    suspend fun delete(notes: Note) {
        return db.deleteNote(notes)
    }

    suspend fun deleteNotesById(id: Int) {
        return db.deleteNotesById(id)
    }

    fun searchNote(query: String?) = db.searchNote(query)

    suspend fun updateIsPinnedColumn(noteIds: List<Int>, isPinned: Int) {
        db.updateIsPinnedColumn(noteIds, isPinned)
    }

    suspend fun updatePriorityColumnInDB(
        noteIds: List<Int>,
        priority: Priority,
        isHighPriorityVisible: Int
    ) {
        db.updatePriorityColumnInDB(noteIds, priority, isHighPriorityVisible)
    }

}