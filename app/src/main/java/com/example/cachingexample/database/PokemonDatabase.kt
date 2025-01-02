package com.example.cachingexample.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cachingexample.database.converter.InstantConverter
import com.example.cachingexample.database.dao.AbilityDao
import com.example.cachingexample.database.dao.PokemonDao
import com.example.cachingexample.database.model.DbAbility
import com.example.cachingexample.database.model.DbPokemon

@Database(
    version = 1,
    entities = [
        DbAbility::class,
        DbPokemon::class,
    ]
)
@TypeConverters(
    InstantConverter::class
)
abstract class PokemonDatabase: RoomDatabase() {
    abstract val abilityDao: AbilityDao
    abstract val pokemonDao: PokemonDao
}