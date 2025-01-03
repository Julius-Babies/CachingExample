package com.example.cachingexample.repository

import com.example.cachingexample.data.source.cache.AbilitySource
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.CacheableItem
import kotlinx.coroutines.flow.Flow

class AbilityRepository(
    private val abilitySource: AbilitySource,
) : CacheableItem<Ability> {
    override fun getAll(configuration: CacheableItem.FetchConfiguration<Ability>): Flow<List<Ability>> {
        TODO("Not yet implemented")
    }

    override fun getById(id: Int, configuration: CacheableItem.FetchConfiguration<Ability>): Flow<Cacheable<Ability>> {
        return abilitySource.getById(id)
    }
}