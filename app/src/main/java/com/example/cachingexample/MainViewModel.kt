package com.example.cachingexample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cachingexample.model.Ability
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.Pokemon
import com.example.cachingexample.repository.PokemonRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {
    var state by mutableStateOf(MainState())
        private set

    private var loadPokemon: Job? = null

    init {
        loadPokemon(state.currentId)
    }

    private fun loadPokemon(id: Int) {
        loadPokemon?.cancel()
        loadPokemon = viewModelScope.launch {
            pokemonRepository.getById(
                id = id,
                configuration = Pokemon.Fetch(
                    abilities = Ability.Fetch()
                )
            ).collect {
                state = state.copy(pokemon = it)
            }
        }
    }

    fun onEvent(event: MainEvent) {
        viewModelScope.launch {
            when (event) {
                MainEvent.IncrementId -> {
                    state = state.copy(currentId = state.currentId + 1)
                    loadPokemon(state.currentId)
                }
                MainEvent.DecrementId -> {
                    state = state.copy(currentId = state.currentId - 1)
                    loadPokemon(state.currentId)
                }
            }
        }
    }
}

data class MainState(
    val pokemon: Cacheable<Pokemon> = Cacheable.Uninitialized("1"),
    val currentId: Int = 1
)

sealed class MainEvent {
    data object IncrementId : MainEvent()
    data object DecrementId : MainEvent()
}