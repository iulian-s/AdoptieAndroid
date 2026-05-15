package com.example.adoptie.anunt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortMenu(
    currentSortOption: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label = when (currentSortOption) {
        SortOption.RECENT -> "Cele mai recente"
        SortOption.TITLE_ASC -> "Nume (A-Z)"
        SortOption.TITLE_DESC -> "Nume (Z-A)"
    }

    Box {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier
                    .widthIn(min = 140.dp)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Sortare")
            }
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
                text = { Text("Nume (Z-A)") },
                onClick = {
                    onSortChange(SortOption.TITLE_DESC)
                    expanded = false
                }
            )
        }
    }
}
