package com.example.cachingexample.model

data class Ability(
    val id: Int,
    val name: String,
    val url: String,
) : CachedEntity {
    override fun getId(): String = id.toString()
}