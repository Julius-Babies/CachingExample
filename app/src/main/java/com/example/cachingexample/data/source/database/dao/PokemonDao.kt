package com.example.cachingexample.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.cachingexample.data.source.database.model.DbPokemon
import com.example.cachingexample.data.source.database.model.crossover.DbPokemonAbilityCrossover
import com.example.cachingexample.data.source.database.model.embedded.EmbeddedPokemon

@Dao
interface PokemonDao {

    @Upsert
    suspend fun upsert(pokemons: List<DbPokemon>, abilities: List<DbPokemonAbilityCrossover>)

    @Query("SELECT * FROM pokemons WHERE id = :id")
    suspend fun getById(id: Int): EmbeddedPokemon?

    @Query("SELECT * FROM pokemons")
    suspend fun getAll(): List<EmbeddedPokemon>
}