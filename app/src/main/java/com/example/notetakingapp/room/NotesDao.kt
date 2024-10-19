package com.example.notetakingapp.room

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
    suspend fun insertnotes(notes: Note)
    @Update
    suspend fun updatenotes(notes: Note)
    @Delete
    suspend fun deletenotes(notes: Note)

    @Query("DELETE FROM Notes_table WHERE noteId =:noteId ")
    suspend fun deleteNotesById(noteId:Int)
    //////////////////////////
    @Query("UPDATE Notes_table SET isSelected = 0")
    suspend fun deselectall()
    //////////////////////////
    @Query("UPDATE Notes_table SET isSelected=:isSelected")
    fun updateAll(isSelected:Int)
    //////////////////
    @Query("SELECT * FROM Notes_table WHERE isSelected = 1")
    fun getSelectedNotes(): LiveData<List<Note>>
    ////////////////
   @Query("SELECT * FROM Notes_table")
    fun getallnotes(): LiveData<List<Note>>
    @Query("SELECT * FROM Notes_table WHERE title LIKE :query OR  description LIKE :query " )
    fun searchNote(query:String?):LiveData<List<Note>>
    @Query("UPDATE Notes_table SET image= :imageArray WHERE noteId=:noteId")
    suspend fun saveImage(imageArray: String, noteId: Int)
    ////////////////
    @Query("UPDATE Notes_table SET isPinned=:isPinned WHERE noteId=:noteId")
    suspend fun updatePinStatus(noteId: Int, isPinned: Boolean)
    //////////////////////////
//    @Query("UPDATE Notes_table SET isSelected=:isSelected WHERE noteId=:noteId")
//     fun  updateselectednotes(noteId: Int ,isSelected:Boolean):LiveData<List<Notes>>

    //This query will update all selected note records according to "isPinned" value
    @Query("UPDATE Notes_table SET isPinned =:isPinned WHERE noteId IN (:noteIds)")
    suspend fun updateIsPinnedColumn(noteIds: List<Int>, isPinned: Int)

}