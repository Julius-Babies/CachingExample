package com.example.cachingexample.data.source.database.converter

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun toInstant(value: Long): Instant {
        return Instant.ofEpochSecond(value)
    }

    @TypeConverter
    fun toLong(value: Instant): Long {
        return value.epochSecond
    }
}