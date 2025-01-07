package com.example.cachingexample.repository

import com.example.cachingexample.App
import com.example.cachingexample.model.Ability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.takeWhile
import kotlin.reflect.KProperty

data class Item<T>(
    val id: String,
    val flow: Flow<ItemFlow<T>>
) {
    suspend fun waitForValue(): T {
        return this.flow.filterIsInstance<ItemFlow.Done<T>>().first().value
    }
}

sealed class ItemFlow<T> {
    data class Uninitialized<T>(val id: String) : ItemFlow<T>()
    data class Loading<T>(val id: String): ItemFlow<T>()
    data class Done<T>(val value: T): ItemFlow<T>()
}

class AbilityDelegate(private val ids: List<Int>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<Item<Ability>> {
        return ids.map { id ->
            App.abilitySource.getById(id)
        }
    }
}