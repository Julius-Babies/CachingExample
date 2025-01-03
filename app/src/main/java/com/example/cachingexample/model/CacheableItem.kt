package com.example.cachingexample.model

import kotlinx.coroutines.flow.Flow

interface CacheableItem<T : CachedItem> {
    fun getAll(configuration: FetchConfiguration<T>): Flow<List<T>>
    fun getById(id: Int, configuration: FetchConfiguration<T>): Flow<Cacheable<T>>

    sealed class FetchConfiguration<T> {
        abstract class Fetch<T> : FetchConfiguration<T>()
        class Ignore<T> : FetchConfiguration<T>()
    }
}

interface CachedItem {
    fun getItemId(): String
}

sealed class Cacheable<T : CachedItem> {
    data class Loaded<T : CachedItem>(val value: T) : Cacheable<T>() {
        override fun getItemId(): String = value.getItemId()
    }
    data class Loading<T : CachedItem>(val id: String, val progress: Int) : Cacheable<T>() {
        override fun getItemId(): String = id
    }
    data class Uninitialized<T : CachedItem>(val id: String) : Cacheable<T>() {
        override fun getItemId(): String = id
    }

    abstract fun getItemId(): String
}