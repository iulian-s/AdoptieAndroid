package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedSpecie: String?,
    selectedRasa: String?,
    selectedVarsta: Varsta?,
    raseMap: Map<String, List<String>>,
    onDismiss: () -> Unit,
    onApplyFilters: (specie: String?, rasa: String?, varsta: Varsta?) -> Unit,

) {
    var tempSpecie by rememberSaveable { mutableStateOf(selectedSpecie) }
    var tempRasa by rememberSaveable { mutableStateOf(selectedRasa) }
    var expandedSpecie by remember { mutableStateOf(false) }
    var expandedRasa by remember { mutableStateOf(false) }
    var tempVarsta by rememberSaveable { mutableStateOf(selectedVarsta) }
    var expandedVarsta by remember { mutableStateOf(false) }

    val availableSpecies = raseMap.keys.toList()


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                Spacer(Modifier.height(16.dp))
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

                    // Opțiunile din Enum
                    Varsta.getAll().forEach { varsta ->
                        DropdownMenuItem(text = { Text(varsta.display) }, onClick = {
                            tempVarsta = varsta
                            expandedVarsta = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            // Buton Aplică
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                // Buton Șterge Filtre (Resetare completă)
                TextButton(onClick = { onApplyFilters(null, null, null); onDismiss() }) { // <-- ADAUGĂ NULL
                    Text("Șterge Filtrele")
                }

                // Buton Aplică
                Button(
                    onClick = {
                        onApplyFilters(tempSpecie, tempRasa, tempVarsta) // <-- TRIMITE VARSTA
                        onDismiss()
                    }
                ) {
                    Text("Aplică")
                }
            }
        }
    }
}