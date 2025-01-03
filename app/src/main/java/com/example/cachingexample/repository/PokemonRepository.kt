package com.example.cachingexample.repository

import com.example.cachingexample.abilityRepository
import com.example.cachingexample.data.source.cache.PokemonSource
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.CacheableItem
import com.example.cachingexample.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PokemonRepository(
    private val pokemonSource: PokemonSource,
) : CacheableItem<Pokemon> {
    override fun getAll(configuration: CacheableItem.FetchConfiguration<Pokemon>): Flow<List<Pokemon>> {
        TODO("Not yet implemented")
    }

    override fun getById(id: Int, configuration: CacheableItem.FetchConfiguration<Pokemon>): Flow<Cacheable<Pokemon>> = channelFlow {
        pokemonSource.getById(id).collectLatest { cacheablePokemon ->
            if (configuration is CacheableItem.FetchConfiguration.Ignore) return@collectLatest send(cacheablePokemon)
            if (configuration is Pokemon.Fetch) {
                if (cacheablePokemon !is Cacheable.Loaded) return@collectLatest send(cacheablePokemon)
                if (configuration.abilities is Ability.Fetch) {
                    val abilities: MutableMap<Int, Cacheable<Ability>> = cacheablePokemon.value
                        .abilities
                        .associate { it.getItemId().toInt() to Cacheable.Uninitialized<Ability>(it.getItemId()) }
                        .toMutableMap()
                    launch {
                        combine(
                            cacheablePokemon.value.abilities
                                .map { it.getItemId().toInt() }
                                .map { abilityRepository.getById(it, configuration.abilities) }
                        ) { cachedAbilities ->
                            abilities.putAll(cachedAbilities.associateBy { it.getItemId().toInt() })
                        }.collectLatest {
                            send(Cacheable.Loaded(cacheablePokemon.value.copy(abilities = abilities.values.toList())))
                        }
                    }
                }
            }

            return@collectLatest send(cacheablePokemon)
        }
    }
}