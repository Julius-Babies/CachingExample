package com.example.cachingexample.model

import com.example.cachingexample.App
import com.example.cachingexample.repository.waitForValue

suspend fun example() {
    val pokemon42 = App.pokemonSource.getById(42).waitForValue()
    val abilities = pokemon42.abilities.waitForValue()
    println(abilities)
}