package com.example.cachingexample.model

data class Ability(
    val id: Int,
    val name: String,
    val url: String
) : CachedItem {
    override fun getItemId(): String = id.toString()

    class Fetch : CacheableItem.FetchConfiguration.Fetch<Ability>()
}