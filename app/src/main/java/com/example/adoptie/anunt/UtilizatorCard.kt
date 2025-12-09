package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_URL
import com.example.adoptie.localitate.LocalitateDTO
import com.example.adoptie.utilizator.UtilizatorDTO

@Composable
fun UtilizatorCard(user: UtilizatorDTO, localitate: LocalitateDTO) {
    val defaultAvatarPath = "/imagini/avatar.png"
    val avatarPath = user.avatar ?: defaultAvatarPath
    val avatarUrl = BASE_URL + avatarPath

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        onClick = { /* TODO: Navigare la profilul utilizatorului */ },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ){
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar ${user.username}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape), // Formă circulară
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = user.nume.ifEmpty { user.username },
                    style = MaterialTheme.typography.titleMedium
                )
                if (user.telefon.isNotEmpty()) {
                    Text(
                        text = "Telefon: ${user.telefon}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "${localitate.nume}, ${localitate.judet}",
                    style = MaterialTheme.typography.bodyMedium
                    )
            }
        }
    }
}