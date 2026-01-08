package com.example.adoptie.utilizator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_URL
import com.example.adoptie.RetrofitClient
import com.example.adoptie.localitate.LocalitateDTO
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilulMeuScreen(onBack: () -> Unit) {
    var userState by remember { mutableStateOf<UtilizatorDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var localitateState by remember{ mutableStateOf<LocalitateDTO?>(null)}

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.utilizatorService.getInfoUtilizator()
            if (response.isSuccessful) {
                userState = response.body()
            }
            localitateState = RetrofitClient.localitateService.getLocalitateDetails(userState?.localitateId)
        } catch (e: Exception) { /* Gestionare eroare */ }
        isLoading = false
    }

    Scaffold(
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }

                Text(
                    text = "Profilul meu",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
            }


            userState?.let { user ->
                Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxWidth()) {
                    // Avatar (Cerc cu inițială)
                    val avatarUrl = BASE_URL+ (user.avatar ?: "/imagini/avatar.png")
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Avatar ${user.username}",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally),

                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(24.dp))
                    InfoRow(label = "Nume", value = user.nume)
                    InfoRow(label = "Email", value = user.username)
                    InfoRow(label = "Telefon", value = user.telefon)
                    InfoRow(label = "Locatie", value = "${localitateState?.nume}, ${localitateState?.judet}")

                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp)
    }
}