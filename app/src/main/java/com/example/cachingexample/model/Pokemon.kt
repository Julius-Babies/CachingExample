package com.example.cachingexample.model

data class Pokemon(
    val id: Int,
    val name: String,
    val abilities: List<Cacheable<Ability>>
) : CachedItem {
    override fun getItemId(): String = id.toString()

    class Fetch(
        val abilities: CacheableItem.FetchConfiguration<Ability>
    ) : CacheableItem.FetchConfiguration.Fetch<Pokemon>()
}