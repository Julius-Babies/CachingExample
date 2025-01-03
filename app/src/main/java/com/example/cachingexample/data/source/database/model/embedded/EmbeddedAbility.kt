package com.example.cachingexample.data.source.database.model.embedded

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cachingexample.data.source.database.model.DbAbility
import com.example.cachingexample.data.source.database.model.crossover.DbPokemonAbilityCrossover
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable

data class EmbeddedAbility(
    @Embedded val ability: DbAbility,
    @Relation(
        parentColumn = "id",
        entityColumn = "ability_id",
    ) val pokemon: List<DbPokemonAbilityCrossover>
) {
    fun toModel(): Ability {
        return Ability(
            id = ability.id,
            name = ability.name,
            url = ability.url,
            pokemon = pokemon.map { Cacheable.Uninitialized(it.pokemonId.toString()) }
        )
    }
}