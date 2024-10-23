package com.note.notetakingapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(notes: Note)
    @Update
    suspend fun updateNote(notes: Note)
    @Delete
    suspend fun deleteNote(notes: Note)

    @Query("DELETE FROM Notes_table WHERE noteId =:noteId ")
    suspend fun deleteNotesById(noteId:Int)

    @Query("UPDATE Notes_table SET isSelected = 0")
    suspend fun deselectAll()

    @Query("UPDATE Notes_table SET isSelected=:isSelected")
    fun updateAll(isSelected:Int)

    @Query("SELECT * FROM Notes_table WHERE isSelected = 1")
    fun getSelectedNotes(): LiveData<List<Note>>

   @Query("SELECT * FROM Notes_table")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Notes_table WHERE title LIKE :query OR  description LIKE :query " )
    fun searchNote(query:String?):LiveData<List<Note>>

    @Query("UPDATE Notes_table SET imagePath= :imageArray WHERE noteId=:noteId")
    suspend fun saveImage(imageArray: String, noteId: Int)

    @Query("UPDATE Notes_table SET isPinned=:isPinned WHERE noteId=:noteId")
    suspend fun updatePinStatus(noteId: Int, isPinned: Boolean)

    //This query will update all selected note records according to "isPinned" value
    @Query("UPDATE Notes_table SET isPinned =:isPinned WHERE noteId IN (:noteIds)")
    suspend fun updateIsPinnedColumn(noteIds: List<Int>, isPinned: Int)

    // This query will update note priority and isHighPriorityVisible column by note ids
    @Query("UPDATE Notes_table SET priority = :priority, isHighPriorityVisible = :isHighPriorityVisible WHERE noteId IN (:noteIds)")
    suspend fun updatePriorityColumnInDB(noteIds: List<Int>, priority: Priority, isHighPriorityVisible: Int)

    // This query will deletes multiple notes
    @Query("DELETE FROM Notes_table WHERE noteId IN (:noteIds)")
    suspend fun deleteNotesByIds(noteIds: List<Int>)
}