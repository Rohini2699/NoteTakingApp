package com.example.notetakingapp.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Utils {
     fun convertMillisToLocalDateTime(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
    fun formatLocalDateTimeWithZoneId(dateTime: LocalDateTime, zoneId: ZoneId): String {
        val zonedDateTime = dateTime.atZone(zoneId)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a" )
        return zonedDateTime.format(formatter)
    }

}