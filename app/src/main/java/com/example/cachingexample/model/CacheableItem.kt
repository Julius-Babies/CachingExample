package com.example.cachingexample.model

import kotlinx.coroutines.flow.Flow

interface CacheableItem<T : CachedItem<T>> {
    fun getAll(configuration: FetchConfiguration<T>): Flow<List<T>>
    fun getById(id: Int, configuration: FetchConfiguration<T>): Flow<Cacheable<T>>

    sealed class FetchConfiguration<T> {
        abstract class Fetch<T> : FetchConfiguration<T>()
        class Ignore<T> : FetchConfiguration<T>()
    }
}

interface CachedItem<T> {
    fun getItemId(): String
    fun isConfigSatisfied(
        configuration: CacheableItem.FetchConfiguration<T>,
        allowLoading: Boolean
    ): Boolean
}

sealed class Cacheable<T : CachedItem<T>> {
    data class Loaded<T : CachedItem<T>>(val value: T) : Cacheable<T>() {
        override fun getItemId(): String = value.getItemId()
        override fun isConfigSatisfied(configuration: CacheableItem.FetchConfiguration<T>, allowLoading: Boolean): Boolean = value.isConfigSatisfied(configuration, allowLoading)
    }
    data class Loading<T : CachedItem<T>>(val id: String, val progress: Int) : Cacheable<T>() {
        override fun getItemId(): String = id
        override fun isConfigSatisfied(configuration: CacheableItem.FetchConfiguration<T>, allowLoading: Boolean): Boolean = allowLoading
    }
    data class Uninitialized<T : CachedItem<T>>(val id: String) : Cacheable<T>() {
        override fun getItemId(): String = id
        override fun isConfigSatisfied(configuration: CacheableItem.FetchConfiguration<T>, allowLoading: Boolean): Boolean = false
    }

    abstract fun getItemId(): String
    abstract fun isConfigSatisfied(configuration: CacheableItem.FetchConfiguration<T>, allowLoading: Boolean): Boolean
}