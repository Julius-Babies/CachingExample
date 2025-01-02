package com.example.cachingexample.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.Pokemon
import java.time.Instant

@Entity(
    tableName = "pokemons",
    primaryKeys = ["id"]
)
data class DbPokemon(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "abilities") val abilities: String,
    @ColumnInfo(name = "cached_at") val cachedAt: Instant,
) {
    fun toModel(): Pokemon? {
        return Pokemon(
            id = id,
            name = name,
            abilities = abilities
                .split("|")
                .map {
                    Cacheable.Uninitialized(it.toIntOrNull()?.toString() ?: return null)
                }
        )
    }
}
