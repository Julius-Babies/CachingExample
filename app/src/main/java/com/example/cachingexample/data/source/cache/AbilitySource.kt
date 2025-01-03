package com.example.cachingexample.data.source.cache

import android.util.Log
import com.example.cachingexample.data.source.database.PokemonDatabase
import com.example.cachingexample.data.source.database.model.DbAbility
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class AbilitySource(
    private val httpClient: HttpClient,
    private val pokemonDatabase: PokemonDatabase
) {
    fun getById(id: Int): Flow<Cacheable<Ability>> = flow {
        pokemonDatabase.abilityDao.getById(id)?.let { return@flow emit(Cacheable.Loaded(it.toModel())) }
        Log.i("AbilityRepository", "Loading $id")
        emit(Cacheable.Loading(id.toString(), 0))
        val url = "https://pokeapi.co/api/v2/ability/$id/"
        val response = httpClient.get(url)
        if (!response.status.isSuccess()) return@flow emit(Cacheable.Uninitialized(id.toString()))
        val data = response.body<AbilityResponse>()
        pokemonDatabase.abilityDao.upsert(listOf(DbAbility(id, data.name, url, Instant.now())))
        emitAll(getById(id))
    }
}

@Serializable
private data class AbilityResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)