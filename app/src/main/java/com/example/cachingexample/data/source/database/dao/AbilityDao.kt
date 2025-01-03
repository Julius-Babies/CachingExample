package com.example.cachingexample.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.cachingexample.data.source.database.model.DbAbility

@Dao
interface AbilityDao {

    @Upsert
    suspend fun upsert(abilities: List<DbAbility>)

    @Query("SELECT * FROM abilities WHERE id = :id")
    suspend fun getById(id: Int): DbAbility?
}