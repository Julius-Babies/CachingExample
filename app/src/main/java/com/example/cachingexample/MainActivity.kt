package com.example.cachingexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cachingexample.database.PokemonDatabase
import com.example.cachingexample.model.Cacheable
import com.example.cachingexample.model.PokemonFinder
import com.example.cachingexample.repository.AbilityRepository
import com.example.cachingexample.repository.PokemonRepository
import com.example.cachingexample.ui.theme.CachingExampleTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KoinApplication(
                application = {
                    modules(
                        module {
                            androidContext(this@MainActivity)
                            single<PokemonDatabase> {
                                Room.databaseBuilder(
                                    context = get(),
                                    name = "pokemon",
                                    klass = PokemonDatabase::class.java
                                )
                                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                                    .build()
                            }

                            single<HttpClient> {
                                HttpClient(CIO) {
                                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                                }
                            }
                            singleOf(::AbilityRepository)
                            singleOf(::PokemonRepository)
                            singleOf(::PokemonFinder)
                            viewModelOf(::MainViewModel)
                        }
                    )
                }
            ) {
                val viewModel = koinViewModel<MainViewModel>()
                val state = viewModel.state

                CachingExampleTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                        ) {
                            Row {
                                Button(
                                    onClick = { viewModel.onEvent(MainEvent.IncrementId) }
                                ) { Text("+") }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.onEvent(MainEvent.DecrementId) }
                                ) { Text("-") }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ID: ${state.currentId}")
                            }
                            when (state.pokemon) {
                                is Cacheable.Uninitialized -> {
                                    Text("Uninitialized")
                                }
                                is Cacheable.Loading -> {
                                    CircularProgressIndicator()
                                }
                                is Cacheable.Loaded -> {
                                    Text(state.pokemon.value.name)
                                    state.pokemon.value.abilities.forEach { ability ->
                                        Text("  $ability")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}