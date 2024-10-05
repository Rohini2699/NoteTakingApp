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
    suspend fun insertnotes(notes: Notes)
    @Update
    suspend fun updatenotes(notes: Notes)
    @Delete
    suspend fun deletenotes(notes: Notes)
    //////////////////////////
    @Query("UPDATE Notes_table SET isSelected = 0")
    suspend fun deselectall()
    //////////////////////////
    @Query("UPDATE Notes_table SET isSelected = 1")
    suspend fun updateAll():Int
    //////////////////
    @Query("SELECT * FROM Notes_table WHERE isSelected = 1")
    fun getSelectedNotes(): LiveData<List<Notes>>
   @Query("SELECT * FROM Notes_table")
    fun getallnotes(): LiveData<List<Notes>>
    @Query("SELECT * FROM Notes_table WHERE title LIKE :query OR  description LIKE :query " )
    fun searchNote(query:String?):LiveData<List<Notes>>
}