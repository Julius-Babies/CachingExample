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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cachingexample.data.source.cache.AbilitySource
import com.example.cachingexample.data.source.cache.PokemonSource
import com.example.cachingexample.data.source.database.PokemonDatabase
import com.example.cachingexample.model.Pokemon
import com.example.cachingexample.model.example
import com.example.cachingexample.repository.ItemFlow
import com.example.cachingexample.ui.theme.CachingExampleTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object App {
    lateinit var abilitySource: AbilitySource
    lateinit var pokemonSource: PokemonSource
}

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
                            singleOf(::AbilitySource)
                            singleOf(::PokemonSource)

                            viewModel { MainViewModel() }
                        }
                    )
                }
            ) {

                App.abilitySource = koinInject()
                App.pokemonSource = koinInject()

                LaunchedEffect(true) {
                    example()
                }

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
                            val s = state.pokemon?.flow?.collectAsState(null)
                            s?.value.let {
                                when (it) {
                                    is ItemFlow.Uninitialized -> {
                                        Text("Uninitialized")
                                    }
                                    is ItemFlow.Loading -> {
                                        CircularProgressIndicator()
                                    }
                                    is ItemFlow.Done<Pokemon> -> {
                                        Text("Pokemon ${it.value.name} hat folgende Fähigkeiten:")
                                        it.value.abilities.forEach { ability ->
                                            val a = ability.flow.collectAsState(null)
                                            a.value.let {
                                                when (it) {
                                                    is ItemFlow.Uninitialized -> Text("Fähigkeit ${ability.id}")
                                                    is ItemFlow.Loading -> CircularProgressIndicator()
                                                    is ItemFlow.Done -> Text("Fähigkeit ${it.value.name}")
                                                    null -> Text("null")
                                                }
                                            }
                                        }
                                        HorizontalDivider()
                                    }
                                    null -> Text("null")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}