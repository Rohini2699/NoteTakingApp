package com.note.notetakingapp.room

import androidx.room.TypeConverter

class PriorityConverter {

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.from(priority) // Return the priority using the "priority" parameter
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.value // Return name of enum. for ex : low or high
    }

}