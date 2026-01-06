package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.adoptie.RetrofitClient
import com.example.adoptie.localitate.LocalitateDTO


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedSpecie: String?,
    selectedRasa: String?,
    selectedVarsta: Varsta?,
    raseMap: Map<String, List<String>>,
    selectedLocalitate: LocalitateDTO?,
    selectedRaza: Double,
    localitati: List<LocalitateDTO>,
    onDismiss: () -> Unit,
    onApplyFilters: (specie: String?, rasa: String?, varsta: Varsta?, judet: String?, localitate: LocalitateDTO?, raza: Double) -> Unit,

    ) {
    var tempSpecie by rememberSaveable { mutableStateOf(selectedSpecie) }
    var tempRasa by rememberSaveable { mutableStateOf(selectedRasa) }
    var expandedSpecie by remember { mutableStateOf(false) }
    var expandedRasa by remember { mutableStateOf(false) }
    var tempVarsta by rememberSaveable { mutableStateOf(selectedVarsta) }
    var expandedVarsta by remember { mutableStateOf(false) }


    // Stări pentru selecția curentă
    var tempJudetSelected by remember { mutableStateOf(selectedLocalitate?.judet) }
    var tempLocalitate by remember { mutableStateOf(selectedLocalitate) }
    var tempRaza by remember { mutableStateOf(selectedRaza) }

    // Liste de date
    var listaJudete by remember { mutableStateOf<List<String>>(emptyList()) }
    var listaOraseByJudet by remember { mutableStateOf<List<LocalitateDTO>>(emptyList()) }

    // Stări pentru UI (Dropdown-uri)
    var expandedJudet by remember { mutableStateOf(false) }
    var expandedOras by remember { mutableStateOf(false) }
    var orasQuery by remember { mutableStateOf("") }
    val oraseFiltrate = remember(listaOraseByJudet, orasQuery) {
        if (orasQuery.isEmpty()) {
            listaOraseByJudet
        } else {
            listaOraseByJudet.filter {
                it.nume.contains(orasQuery, ignoreCase = true)
            }
        }
    }


    val availableSpecies = raseMap.keys.toList()

    LaunchedEffect(Unit) {
        listaJudete = RetrofitClient.localitateService.getJudete()
    }
    LaunchedEffect(tempJudetSelected) {
        if (tempJudetSelected != null) {
            try {
                listaOraseByJudet = RetrofitClient.localitateService.getByJudet(tempJudetSelected!!)
            } catch (e: Exception) { /* Log eroare */
            }
        } else {
            listaOraseByJudet = emptyList()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Filtrează Anunțurile", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Selector Specie
            Text("Specie:")
            ExposedDropdownMenuBox(
                expanded = expandedSpecie,
                onExpandedChange = { expandedSpecie = !expandedSpecie },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = tempSpecie ?: "Toate speciile",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpecie) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedSpecie,
                    onDismissRequest = { expandedSpecie = false }
                ) {
                    // Opțiune implicită (Fără filtru)
                    DropdownMenuItem(text = { Text("Toate speciile") }, onClick = {
                        tempSpecie = null
                        tempRasa = null // Resetează rasa când specia se schimbă
                        expandedSpecie = false
                    })
                    Divider()

                    availableSpecies.forEach { specie ->
                        DropdownMenuItem(text = { Text(specie) }, onClick = {
                            tempSpecie = specie
                            tempRasa = null
                            expandedSpecie = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Selector Rasă
            if (tempSpecie != null) {
                Text("Rasă:", style = MaterialTheme.typography.titleMedium)
                ExposedDropdownMenuBox(
                    expanded = expandedRasa,
                    onExpandedChange = { expandedRasa = !expandedRasa },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    val availableRases = raseMap[tempSpecie] ?: emptyList()
                    OutlinedTextField(
                        value = tempRasa ?: "Toate rasele",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRasa) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedRasa,
                        onDismissRequest = { expandedRasa = false }
                    ) {
                        DropdownMenuItem(text = { Text("Toate rasele") }, onClick = {
                            tempRasa = null
                            expandedRasa = false
                        })
                        Divider()
                        availableRases.forEach { rasa ->
                            DropdownMenuItem(text = { Text(rasa) }, onClick = {
                                tempRasa = rasa
                                expandedRasa = false
                            })
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))




            Text("Vârstă:", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = expandedVarsta,
                onExpandedChange = { expandedVarsta = !expandedVarsta },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = tempVarsta?.display ?: "Toate vârstele",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVarsta) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedVarsta,
                    onDismissRequest = { expandedVarsta = false }
                ) {
                    // Opțiune implicită (Fără filtru)
                    DropdownMenuItem(text = { Text("Toate vârstele") }, onClick = {
                        tempVarsta = null
                        expandedVarsta = false
                    })
                    Divider()

                    // Optiunile din Enum
                    Varsta.getAll().forEach { varsta ->
                        DropdownMenuItem(text = { Text(varsta.display) }, onClick = {
                            tempVarsta = varsta
                            expandedVarsta = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))


            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Locație", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Text("Județ:", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = expandedJudet,
                onExpandedChange = { expandedJudet = !expandedJudet }
            ) {
                OutlinedTextField(
                    value = tempJudetSelected ?: "Alege județul",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJudet) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedJudet,
                    onDismissRequest = { expandedJudet = false }) {
                    DropdownMenuItem(
                        text = { Text("Toată țara") },
                        onClick = {
                            tempJudetSelected = null
                            tempLocalitate = null
                            expandedJudet = false
                        }
                    )
                    listaJudete.forEach { judet ->
                        DropdownMenuItem(
                            text = { Text(judet) },
                            onClick = {
                                tempJudetSelected = judet
                                tempLocalitate = null // Resetează orașul la schimbarea județului
                                expandedJudet = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- SELECTOR ORAȘ (Apare doar dacă județul e selectat) ---
            if (tempJudetSelected != null) {
                Text("Oraș:", style = MaterialTheme.typography.titleMedium)
                ExposedDropdownMenuBox(
                    expanded = expandedOras,
                    onExpandedChange = { expandedOras = !expandedOras }
                ) {
                    OutlinedTextField(
                        value = tempLocalitate?.nume ?: "Alege orașul",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOras) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedOras, onDismissRequest = {
                        expandedOras = false
                        orasQuery = ""
                    }
                    ) {
                        OutlinedTextField(
                            value = orasQuery,
                            onValueChange = { orasQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            placeholder = { Text("Caută oraș...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )

                        // Lista de orașe filtrată
                        Box(modifier = Modifier.heightIn(max = 250.dp)){
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                            ) {
                                oraseFiltrate.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc.nume) },
                                        onClick = {
                                            tempLocalitate = loc
                                            expandedOras = false
                                            orasQuery = "" // Resetăm după selecție
                                        }
                                    )
                                }

                                if (oraseFiltrate.isEmpty()) {
                                    Text(
                                        "Niciun rezultat",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                }
                            }
                        }

                    }



                }
                // --- SLIDER RAZĂ ---
                if (tempLocalitate != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Caută pe o rază de: ${tempRaza.toInt()} km",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Slider(
                        value = tempRaza.toFloat(),
                        onValueChange = { tempRaza = it.toDouble() },
                        valueRange = 5f..200f,
                        steps = 19
                    )
                }

                Spacer(Modifier.height(32.dp))


                // Buton Aplica
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Buton Șterge Filtre (Resetare completă)
                    TextButton(onClick = {
                        onApplyFilters(
                            null,
                            null,
                            null,
                            null,
                            null,
                            50.0
                        ); onDismiss()
                    }) {
                        Text("Șterge Filtrele")
                    }

                    Button(
                        onClick = {
                            onApplyFilters(
                                tempSpecie,
                                tempRasa,
                                tempVarsta,
                                tempJudetSelected,
                                tempLocalitate,
                                tempRaza
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Aplică")
                    }
                }
            }
        }
    }
}
