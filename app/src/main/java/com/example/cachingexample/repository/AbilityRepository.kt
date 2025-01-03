package com.example.cachingexample.repository

import com.example.cachingexample.data.source.cache.AbilitySource
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.CacheableItem
import com.example.cachingexample.model.Pokemon
import com.example.cachingexample.pokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AbilityRepository(
    private val abilitySource: AbilitySource,
) : CacheableItem<Ability> {
    override fun getAll(configuration: CacheableItem.FetchConfiguration<Ability>): Flow<List<Ability>> {
        TODO("Not yet implemented")
    }

    override fun getById(id: Int, configuration: CacheableItem.FetchConfiguration<Ability>): Flow<Cacheable<Ability>> = channelFlow {
        abilitySource.getById(id).collectLatest { cacheableAbility ->
            if (configuration is CacheableItem.FetchConfiguration.Ignore) return@collectLatest send(cacheableAbility)
            if (configuration is Ability.Fetch) {
                if (cacheableAbility !is Cacheable.Loaded) return@collectLatest send(cacheableAbility)
                if (configuration.pokemon is Pokemon.Fetch) {
                    val pokemon: MutableMap<Int, Cacheable<Pokemon>> = cacheableAbility.value
                        .pokemon
                        .associate { it.getItemId().toInt() to Cacheable.Uninitialized<Pokemon>(it.getItemId()) }
                        .toMutableMap()
                    launch {
                        combine(
                            cacheableAbility.value.pokemon
                                .map { it.getItemId().toInt() }
                                .map { pokemonRepository.getById(it, configuration.pokemon) }
                        ) { cachedPokemon ->
                            pokemon.putAll(cachedPokemon.associateBy { it.getItemId().toInt() })
                        }.collectLatest {
                            send(Cacheable.Loaded(cacheableAbility.value.copy(pokemon = pokemon.values.toList())))
                        }
                    }
                }
            }

            return@collectLatest send(cacheableAbility)
        }
    }
}