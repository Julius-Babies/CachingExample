package com.example.cachingexample.model

data class Ability(
    val id: Int,
    val name: String,
    val url: String,
    val pokemon: List<Cacheable<Pokemon>>
) : CachedItem<Ability> {
    override fun getItemId(): String = id.toString()
    override fun isConfigSatisfied(configuration: CacheableItem.FetchConfiguration<Ability>, allowLoading: Boolean): Boolean {
        if (configuration !is Fetch) return true

        if (configuration.pokemon is Pokemon.Fetch && pokemon.any { (it is Cacheable.Loading && !allowLoading) || it !is Cacheable.Loaded || !it.isConfigSatisfied(configuration.pokemon, allowLoading) }) return false

        return true
    }

    class Fetch(
        val pokemon: CacheableItem.FetchConfiguration<Pokemon> = Ignore()
    ) : CacheableItem.FetchConfiguration.Fetch<Ability>()
}