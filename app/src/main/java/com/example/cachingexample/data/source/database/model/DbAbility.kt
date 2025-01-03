package com.example.cachingexample.data.source.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.Instant

@Entity(
    tableName = "abilities",
    primaryKeys = ["id"]
)
data class DbAbility(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "cached_at") val cachedAt: Instant,
)