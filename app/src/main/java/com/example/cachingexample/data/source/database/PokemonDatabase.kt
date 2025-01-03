package com.example.cachingexample.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cachingexample.data.source.database.converter.InstantConverter
import com.example.cachingexample.data.source.database.dao.AbilityDao
import com.example.cachingexample.data.source.database.dao.PokemonDao
import com.example.cachingexample.data.source.database.model.DbAbility
import com.example.cachingexample.data.source.database.model.DbPokemon
import com.example.cachingexample.data.source.database.model.crossover.DbPokemonAbilityCrossover

@Database(
    version = 1,
    entities = [
        DbAbility::class,
        DbPokemon::class,
        DbPokemonAbilityCrossover::class
    ]
)
@TypeConverters(
    InstantConverter::class
)
abstract class PokemonDatabase: RoomDatabase() {
    abstract val abilityDao: AbilityDao
    abstract val pokemonDao: PokemonDao
}