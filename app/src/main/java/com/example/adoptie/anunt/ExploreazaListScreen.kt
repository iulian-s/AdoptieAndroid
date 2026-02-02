package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.adoptie.RetrofitClient
import com.example.adoptie.localitate.LocalitateDTO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreazaListScreen(
    onNavigateToDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var anunturiState by remember {
        mutableStateOf<AnunturiState>(AnunturiState.Loading)
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    //Stare sortare
    var currentSortOption by rememberSaveable { mutableStateOf(SortOption.RECENT) }

    //refresh
    val scope = rememberCoroutineScope ()
    var isRefreshing by remember { mutableStateOf(false) }

    // Stările specifice filtrelor
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var selectedSpecie by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedRasa by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedVarsta by rememberSaveable { mutableStateOf<Varsta?>(null) }
    var raseMap by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var selectedLocalitate by rememberSaveable { mutableStateOf<LocalitateDTO?>(null) }
    var selectedJudet by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedRaza by rememberSaveable { mutableDoubleStateOf(50.0) }
    var allLocalitati by remember { mutableStateOf<List<LocalitateDTO>>(emptyList()) }

    suspend fun fetchAnunturi() {
        try {
            val results = when {
                selectedLocalitate != null -> {
                    RetrofitClient.anuntService.getAnunturiInRaza(selectedLocalitate!!.id, selectedRaza)
                }
                selectedJudet != null -> {
                    RetrofitClient.anuntService.getAnunturiByJudet(selectedJudet!!)
                }
                else -> {
                    RetrofitClient.anuntService.getAnunturiActive()
                }
            }
            anunturiState = AnunturiState.Success(results)
        } catch (e: Exception) {
            anunturiState = AnunturiState.Error("Eroare: ${e.message}")
        }

        try {
            raseMap = RetrofitClient.animaluteService.getRase()

        } catch (e: Exception) {
            // Opțional: arată o eroare dacă nu se pot încărca filtrele
            println("Eroare la încărcarea raselor: ${e.message}")
        }

        try {
            allLocalitati = RetrofitClient.localitateService.getAllLocalitati()
        } catch (e: Exception) {
            println("Eroare localități: ${e.message}")
        }
    }

//    LaunchedEffect(Unit) {
//        val apiService = RetrofitClient.anuntService
//        anunturiState = AnunturiState.Loading
//        anunturiState = try {
//            if (selectedLocalitate != null){
//                val results = apiService.getAnunturiInRaza(
//                    selectedLocalitate!!.id,
//                    selectedRaza
//                )
//                AnunturiState.Success(results)
//            }
//            else{
//                val all = apiService.getAnunturiActive()
//                AnunturiState.Success(all)
//            }
//
//        } catch (e: Exception){
//            AnunturiState.Error("Eroare la incarcarea anunturilor: ${e.message}")
//        }
//
//        try {
//            raseMap = RetrofitClient.animaluteService.getRase()
//
//        } catch (e: Exception) {
//            // Opțional: arată o eroare dacă nu se pot încărca filtrele
//            println("Eroare la încărcarea raselor: ${e.message}")
//        }
//
//        try {
//            allLocalitati = RetrofitClient.localitateService.getAllLocalitati()
//        } catch (e: Exception) {
//            println("Eroare localități: ${e.message}")
//        }
//    }

    LaunchedEffect(selectedLocalitate,selectedJudet, selectedRaza) {
        anunturiState = AnunturiState.Loading
        fetchAnunturi()
        try {
            val results = when {
                // Scenariul 1: Avem localitate selectată -> Căutare pe rază
                selectedLocalitate != null -> {
                    RetrofitClient.anuntService.getAnunturiInRaza(
                        localitateId = selectedLocalitate!!.id,
                        razaKm = selectedRaza
                    )
                }
                // Scenariul 2: Avem doar județul selectat -> Filtrare pe județ
                selectedJudet != null -> {
                    // Va trebui să adaugi acest endpoint în AnunturiApiService
                    RetrofitClient.anuntService.getAnunturiByJudet(selectedJudet!!)
                }
                // Scenariul 3: Nimic selectat -> Toate anunțurile
                else -> {
                    RetrofitClient.anuntService.getAnunturiActive()
                }
            }
            anunturiState = AnunturiState.Success(results)
        } catch (e: Exception) {
            anunturiState = AnunturiState.Error("Eroare: ${e.message}")
        }
    }

    Column(modifier = modifier.fillMaxSize()) {


        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
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


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = { showFilterSheet = true }) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Filtre")
                Spacer(Modifier.width(4.dp))
                // Afișăm dacă există filtre active
                Text("Filtre")
            }

            // Meniu Dropdown pentru Sortare
            SortMenu(
                currentSortOption = currentSortOption,
                onSortChange = { currentSortOption = it }
            )
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    fetchAnunturi()
                    isRefreshing = false
                }
            },
            modifier = Modifier.weight(1f) // Ocupă spațiul rămas sub search
        ) {
            when (val state = anunturiState) {
                is AnunturiState.Loading -> {
                    if (!isRefreshing) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is AnunturiState.Error -> {
                    var showError by remember { mutableStateOf(false) }
                    LaunchedEffect(state) {
                        showError = false
                        delay(3000) // 3 secunde întârziere
                        showError = true
                    }
                    if(showError){
                        Box(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                        }
                    }

                }

                is AnunturiState.Success -> {

                    val filteredAndSortedAnunturi = remember(
                        state.anunturi,
                        searchQuery,
                        currentSortOption,
                        selectedSpecie,
                        selectedRasa,
                        selectedVarsta // Adăugăm toți parametrii de filtrare aici
                    ) {
                        var list = state.anunturi.toList()

                        // A) Filtrare după căutare (Titlu/Descriere)
                        if (searchQuery.isNotBlank()) {
                            val query = searchQuery.lowercase(Locale.getDefault())
                            list = list.filter { anunt ->
                                anunt.titlu.lowercase(Locale.getDefault()).contains(query) ||
                                        anunt.descriere.lowercase(Locale.getDefault())
                                            .contains(query) ||
                                        anunt.specie.lowercase(Locale.getDefault())
                                            .contains(query) ||
                                        anunt.rasa.lowercase(Locale.getDefault()).contains(query)

                            }
                        }

                        // B) Filtrare după Specie
                        if (selectedSpecie != null) {
                            list = list.filter { it.specie == selectedSpecie }
                        }

                        // C) Filtrare după Rasă
                        if (selectedRasa != null) {
                            list = list.filter { it.rasa == selectedRasa }
                        }

                        //filtrare dupa varsta
                        if (selectedVarsta != null) {
                            list = list.filter { it.varsta == selectedVarsta }
                        }

                        // D) Sortare
                        when (currentSortOption) {
                            SortOption.RECENT -> list.sortedByDescending { it.updatedAt }
                            SortOption.TITLE_ASC -> list.sortedBy { it.titlu }
                            SortOption.TITLE_DESC -> list.sortedByDescending { it.titlu }
                        }
                    }

                    if (filteredAndSortedAnunturi.isEmpty() && searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nu s-au gasit anunturi!")
                        }
                    } else if (filteredAndSortedAnunturi.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nu exista anunturi active in acest moment.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            items(filteredAndSortedAnunturi) { anunt ->
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
        if (showFilterSheet) {
            FilterBottomSheet(
                selectedSpecie = selectedSpecie,
                selectedRasa = selectedRasa,
                selectedVarsta = selectedVarsta,
                raseMap = raseMap,
                localitati = allLocalitati,
                selectedLocalitate = selectedLocalitate,
                selectedRaza = selectedRaza,
                onDismiss = { showFilterSheet = false },
                onApplyFilters = { specie, rasa, varsta, judet, localitate, raza ->
                    selectedSpecie = specie
                    selectedRasa = rasa
                    selectedVarsta = varsta
                    selectedJudet = judet
                    selectedLocalitate = localitate
                    selectedRaza = raza
                    showFilterSheet = false
                },

                )
        }
    }
}