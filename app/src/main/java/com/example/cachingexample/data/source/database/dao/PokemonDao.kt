package com.example.cachingexample.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.cachingexample.data.source.database.model.DbPokemon

@Dao
interface PokemonDao {

    @Upsert
    suspend fun upsert(pokemons: List<DbPokemon>)

    @Query("SELECT * FROM pokemons WHERE id = :id")
    suspend fun getById(id: Int): DbPokemon?

    @Query("SELECT * FROM pokemons")
    suspend fun getAll(): List<DbPokemon>
}