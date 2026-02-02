package com.example.adoptie.anunt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_IMAGE_URL
import com.example.adoptie.localitate.LocalitateDTO
import com.example.adoptie.utilizator.UtilizatorDTO
import com.example.adoptie.utilizator.initiatePhoneCall

@Composable
fun UtilizatorCard(user: UtilizatorDTO, localitate: LocalitateDTO, onCardClick: () -> Unit) {
    val defaultAvatarPath = "/imagini/avatar.png"
    val avatarPath = user.avatar ?: defaultAvatarPath
    val avatarUrl = BASE_IMAGE_URL + avatarPath
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        onClick = onCardClick,
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
                    Row(
                        modifier = Modifier
                            .clickable{
                                initiatePhoneCall(context, user.telefon)
                            }
                            .padding(vertical = 4.dp)
                            ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "Apel",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user.telefon,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                }
                Text(
                    text = "${localitate.nume}, ${localitate.judet}",
                    style = MaterialTheme.typography.bodyMedium
                    )
            }
        }
    }
}