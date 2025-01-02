package com.example.cachingexample.repository

import android.util.Log
import com.example.cachingexample.database.PokemonDatabase
import com.example.cachingexample.database.model.DbPokemon
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.Pokemon
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class PokemonRepository(
    private val httpClient: HttpClient,
    private val pokemonDatabase: PokemonDatabase
) {
    fun getById(id: Int): Flow<Cacheable<Pokemon>> = flow {
        pokemonDatabase.pokemonDao.getById(id)?.toModel()?.let { return@flow emit(Cacheable.Loaded(it)) }
        emit(Cacheable.Loading(0, id.toString()))
        val url = "https://pokeapi.co/api/v2/pokemon/$id/"
        val response = httpClient.get(url)
        if (!response.status.isSuccess()) return@flow emit(Cacheable.Uninitialized(id.toString()))
        Log.i("PokemonRepository", "Response: ${response.bodyAsText()}")
        val data = response.body<PokemonResponse>()
        val pokemon = Pokemon(id, data.name, data.abilities.map<PokemonAbility, Cacheable.Uninitialized<Ability>> {
            Cacheable.Uninitialized(it.ability.url.split("/").last { pathSegment -> pathSegment.isNotEmpty() })
        }.sortedBy { it.getId() })
        pokemonDatabase.pokemonDao.upsert(listOf(
            DbPokemon(
                id = pokemon.id,
                name = pokemon.name,
                abilities = pokemon.abilities.joinToString("|") { it.getId() },
                cachedAt = Instant.now()
            )
        ))
        emitAll(getById(id))
    }
}

@Serializable
private data class PokemonResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("abilities") val abilities: List<PokemonAbility>
)

@Serializable
data class PokemonAbility(
    @SerialName("ability") val ability: PokemonAbilityResponse
)

@Serializable
data class PokemonAbilityResponse(
    @SerialName("url") val url: String
)