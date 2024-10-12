package com.example.notetakingapp.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


enum class Priority {
    HIGH,
    LOW
}

@Entity(tableName = "Notes_table")
@Parcelize
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String?,
    var description: String?,

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false,
    var isPinned: Boolean = false,
    val date:String?, // storing date as a time stamp
    var priority: Priority? = null, // Default value for the priority.
    var isHighPriorityVisible: Boolean = false
) : Parcelable
