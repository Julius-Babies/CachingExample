package com.example.cachingexample.model

data class Pokemon(
    val id: Int,
    val name: String,
    val abilities: List<Cacheable<Ability>>
) : CachedItem<Pokemon> {
    override fun getItemId(): String = id.toString()
    override fun isConfigSatisfied(configuration: CacheableItem.FetchConfiguration<Pokemon>, allowLoading: Boolean): Boolean {
        if (configuration !is Fetch) return true
        if (configuration.abilities is Ability.Fetch && abilities.any { (it is Cacheable.Loading && !allowLoading) || it !is Cacheable.Loaded || !it.isConfigSatisfied(configuration.abilities, allowLoading) }) return false
        return true
    }

    class Fetch(
        val abilities: CacheableItem.FetchConfiguration<Ability> = Ignore()
    ) : CacheableItem.FetchConfiguration.Fetch<Pokemon>()
}