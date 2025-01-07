package com.example.cachingexample.model

import com.example.cachingexample.repository.AbilityDelegate

class Pokemon(
    val id: Int,
    val name: String,
    abilityIds: List<Int>
) {
    val abilities by AbilityDelegate(abilityIds)
}