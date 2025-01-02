package com.example.cachingexample.model

interface CachedEntity {
    fun getId(): String
}

sealed class Cacheable<T : CachedEntity>(
    val getId: () -> String
) {
    data class Uninitialized<T : CachedEntity>(val id: String) : Cacheable<T>({ id })
    data class Loading<T : CachedEntity>(val percent: Int, val id: String) : Cacheable<T>({ id })

    data class Loaded<T : CachedEntity>(val value: T) : Cacheable<T>({ value.getId() })
}

sealed class NestedItemConfiguration {
    data object Fetch : NestedItemConfiguration()
    data object Ignore : NestedItemConfiguration()
}