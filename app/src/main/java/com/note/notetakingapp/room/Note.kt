package com.note.notetakingapp.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


enum class Priority(val value: String) {
    HIGH("HIGH"),
    LOW("LOW"),
    NONE("NONE");

    companion object {
        fun from(findValue: String?): Priority =
            Priority.entries.find {
                it.value == (findValue ?: "")
            } ?: NONE
    }
}

@Entity(tableName = "Notes_table")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noteId")
    val id: Int,
    var title: String?,
    var description: String?,
    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false,
    @ColumnInfo(name = "isPinned")
    var isPinned: Boolean = false,
    val date: String?, // storing date as a time stamp
    val imagePath: String? = null,
    @ColumnInfo(name = "priority")
    var priority: Priority = Priority.NONE, // Default value for the priority.
    var isHighPriorityVisible: Boolean = false
) : Parcelable
