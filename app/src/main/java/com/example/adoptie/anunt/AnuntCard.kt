package com.example.adoptie.anunt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_IMAGE_URL
import com.example.adoptie.formatRelativeDate

@Composable
fun AnuntCard(anunt: AnuntDTO, onCardClick: () -> Unit) {

    val imageUrl = if(anunt.listaImagini.isNotEmpty()){
        BASE_IMAGE_URL + anunt.listaImagini.first()
    }
    else{
        null
    }

    val imageModifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick()},
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = imageModifier) {

                // 1. Stratul de Fundal (Blurred Background)
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null, // Fundalul nu are nevoie de descriere
                    modifier = Modifier
                        .fillMaxSize()
                        // blur in margini
                        .blur(radius = 24.dp),

                    // Umple containerul (deci blur-ul va umple tot spatiul)
                    contentScale = ContentScale.Crop,

                    // filtru de culare
                    alpha = 0.5f
                )

                // 2. imaginea principala
                AsyncImage(
                    model = imageUrl,
                    contentDescription = anunt.titlu,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
            Column(modifier = Modifier.padding(16.dp)){
                Text(
                    text = anunt.titlu,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = anunt.rasa, style = MaterialTheme.typography.bodyMedium)
                    Text(text = ", ${anunt.gen.toString().lowercase()},", style = MaterialTheme.typography.bodyMedium)
                    Text(text = " Vârsta: ${anunt.varsta.display}", style = MaterialTheme.typography.bodyMedium)
                }
                val formattedDate = formatRelativeDate(anunt.updatedAt)
                if (formattedDate.isNotEmpty()) {
                    Text(
                        text = "Actualizat: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}