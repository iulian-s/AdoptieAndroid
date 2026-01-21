package com.example.adoptie.anunt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortMenu(
    currentSortOption: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Afișează opțiunea curentă + iconiță de sortare
    Box{
        Row(
            modifier = Modifier.clickable { expanded = true }
                .widthIn(min = 160.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = when (currentSortOption) {
                    SortOption.RECENT -> "Cele mai recente"
                    SortOption.TITLE_ASC -> "Nume (A-Z)"
                    SortOption.TITLE_DESC -> "Nume (Z-A)"
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Opțiuni sortare")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Cele mai recente") },
                onClick = {
                    onSortChange(SortOption.RECENT)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Nume (A-Z)") },
                onClick = {
                    onSortChange(SortOption.TITLE_ASC)
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {Text("Nume (Z-A)")},
                onClick = {
                    onSortChange(SortOption.TITLE_DESC)
                    expanded = false
                }
            )
    }

    }
}