package com.example.cachingexample.data.source.database.model.embedded

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cachingexample.data.source.database.model.DbPokemon
import com.example.cachingexample.data.source.database.model.crossover.DbPokemonAbilityCrossover
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.Pokemon

data class EmbeddedPokemon(
    @Embedded val pokemon: DbPokemon,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemon_id",
    ) val abilities: List<DbPokemonAbilityCrossover>
) {
    fun toModel(): Pokemon {
        return Pokemon(
            id = pokemon.id,
            name = pokemon.name,
            abilities = abilities.map { Cacheable.Uninitialized(it.abilityId.toString()) }
        )
    }
}