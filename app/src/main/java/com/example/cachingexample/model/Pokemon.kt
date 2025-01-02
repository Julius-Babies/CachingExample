package com.example.cachingexample.model

import com.example.cachingexample.repository.AbilityRepository
import com.example.cachingexample.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class Pokemon(
    val id: Int,
    val name: String,
    val abilities: List<Cacheable<Ability>>
) : CachedEntity {
    override fun getId(): String = id.toString()

    class NestedElementConfiguration(
        val abilities: NestedItemConfiguration = NestedItemConfiguration.Ignore
    )

    fun isConfigurationApplied(configuration: NestedElementConfiguration): Boolean {
        if (configuration.abilities == NestedItemConfiguration.Fetch && abilities.any { it !is Cacheable.Loaded }) return false

        return true
    }
}

class PokemonFinder(
    private val pokemonRepository: PokemonRepository,
    private val abilityRepository: AbilityRepository
) {
    operator fun invoke(
        id: Int,
        configuration: Pokemon.NestedElementConfiguration = Pokemon.NestedElementConfiguration()
    ): Flow<Cacheable<Pokemon>> = flow {
        pokemonRepository.getById(id).collect { pokemonCacheable ->
            if (pokemonCacheable !is Cacheable.Loaded) return@collect emit(pokemonCacheable)
            var pokemon = pokemonCacheable.value
            val abilityIds = pokemon.abilities.map { it.getId() }
            emit(Cacheable.Loaded(pokemon))
            if (configuration.abilities == NestedItemConfiguration.Fetch) {
                abilityIds.forEach { abilityId ->
                    abilityRepository.getById(abilityId.toInt()).collect { loadedAbility ->
                        pokemon = pokemon.copy(
                            abilities = pokemon.abilities.map { existingPokemonAbility ->
                                if (existingPokemonAbility.getId() == loadedAbility.getId()) loadedAbility else existingPokemonAbility
                            }
                        )
                        emit(Cacheable.Loaded(pokemon))
                    }
                }
            }
        }
    }
}