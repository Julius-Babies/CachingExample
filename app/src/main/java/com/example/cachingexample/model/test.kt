package com.example.cachingexample.model

import com.example.cachingexample.App

suspend fun example() {
    val pokemon42 = App.pokemonSource.getById(42).waitForValue()
    val abilities = pokemon42.abilities.map { it.waitForValue() }
    println(abilities)
}