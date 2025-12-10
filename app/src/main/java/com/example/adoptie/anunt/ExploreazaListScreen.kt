package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.adoptie.RetrofitClient

@Composable
fun ExploreazaListScreen(
    onNavigateToDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var anunturiState by remember {
        mutableStateOf<AnunturiState>(AnunturiState.Loading)
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val apiService = RetrofitClient.anuntService

        anunturiState = try {
            //  Aici are loc apelul  localhost:8080/api/anunturi/active
            val results = apiService.getAnunturiActive()
            AnunturiState.Success(results)
        } catch (e: Exception){
            AnunturiState.Error("Eroare la incarcarea anunturilor: ${e.message}")
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {searchQuery = it},
            label = { Text("Cautare...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Cautare") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Sterge cautarea")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)

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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is AnunturiState.Success -> {
                val anunturiFiltrate = remember(state.anunturi, searchQuery) {
                    if(searchQuery.isBlank()){
                        state.anunturi
                    } else {
                        val query = searchQuery.lowercase()
                        state.anunturi.filter{ anunt ->
                            anunt.titlu.lowercase().contains(query) ||
                                    anunt.descriere.lowercase().contains(query)
                        }
                    }
                }
                if(anunturiFiltrate.isEmpty() && searchQuery.isNotEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text("Nu s-au gasit anunturi!")
                    }
                }
                else if (anunturiFiltrate.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nu exista anunturi active in acest moment.")
                    }
                }
                else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ){
                        items(anunturiFiltrate) { anunt ->
                            AnuntCard(
                                anunt = anunt,
                                onCardClick = {
                                    onNavigateToDetails(anunt.id)
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}