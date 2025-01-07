package com.example.cachingexample.data.source.cache

import android.util.Log
import com.example.cachingexample.data.source.database.PokemonDatabase
import com.example.cachingexample.data.source.database.model.DbPokemon
import com.example.cachingexample.data.source.database.model.crossover.DbPokemonAbilityCrossover
import com.example.cachingexample.model.Pokemon
import com.example.cachingexample.repository.Item
import com.example.cachingexample.repository.ItemFlow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class PokemonSource(
    private val httpClient: HttpClient,
    private val pokemonDatabase: PokemonDatabase
) {

    fun getById(id: Int): Item<Pokemon> {
        return Item(
            id = id.toString(),
            flow = flow {
                pokemonDatabase.pokemonDao.getById(id)?.toModel()
                    ?.let { return@flow emit(ItemFlow.Done(it)) }
                emit(ItemFlow.Loading(id.toString()))
                val url = "https://pokeapi.co/api/v2/pokemon/$id/"
                val response = httpClient.get(url)
                if (!response.status.isSuccess()) return@flow emit(ItemFlow.Uninitialized(id.toString()))
                Log.i("PokemonRepository", "Response: ${response.bodyAsText()}")
                val data = response.body<PokemonResponse>()
                val pokemon = Pokemon(
                    id,
                    data.name,
                    data.abilities.map {
                        it.ability.url.split("/").last { it.isNotBlank() }.toInt()
                    })
                pokemonDatabase.pokemonDao.upsert(
                    pokemons = listOf(
                        DbPokemon(
                            id = pokemon.id,
                            name = pokemon.name,
                            cachedAt = Instant.now()
                        )
                    ),
                    abilities = pokemon.abilities.map {
                        DbPokemonAbilityCrossover(
                            pokemon.id,
                            it.id.toInt()
                        )
                    }
                )
                emit(ItemFlow.Done(pokemon))
            }
        )
    }
}
@Serializable
private data class PokemonResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("abilities") val abilities: List<PokemonAbility>
)

@Serializable
private data class PokemonAbility(
    @SerialName("ability") val ability: PokemonAbilityResponse
)

@Serializable
private data class PokemonAbilityResponse(
    @SerialName("url") val url: String
)