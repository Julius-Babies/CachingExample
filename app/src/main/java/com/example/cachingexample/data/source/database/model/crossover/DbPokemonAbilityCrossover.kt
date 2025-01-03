package com.example.cachingexample.data.source.database.model.crossover

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "pokemon_abilities",
    primaryKeys = ["pokemon_id", "ability_id"],
)
data class DbPokemonAbilityCrossover(
    @ColumnInfo(name = "pokemon_id") val pokemonId: Int,
    @ColumnInfo(name = "ability_id") val abilityId: Int,
)