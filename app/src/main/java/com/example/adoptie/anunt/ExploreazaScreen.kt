package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExploreazaScreen(modifier: Modifier = Modifier) {
    var anunturiState by remember {
        mutableStateOf<AnunturiState>(AnunturiState.Loading)
    }
    LaunchedEffect(Unit) {
        anunturiState = try {
            val results = fetchAnunturi() //aici ar trebui api ul
            AnunturiState.Success(results)
        } catch (e: Exception){
            AnunturiState.Error("Eroare la incarcarea anunturilor: ${e.message}")
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Pagina exploreaza",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        when(val state = anunturiState){
            is AnunturiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AnunturiState.Error -> {
                // Afișează mesajul de eroare
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is AnunturiState.Success -> {
                if(state.anunturi.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text("Nu exista anunturi active")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        items(state.anunturi) { anunt ->
                            AnuntCard(anunt = anunt)
                        }
                    }
                }
            }
        }
    }
}
