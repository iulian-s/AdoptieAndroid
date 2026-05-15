@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.adoptie.anunt

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.adoptie.ui.components.CategoryChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.adoptie.RetrofitClient
import com.example.adoptie.localitate.LocalitateDTO
import com.example.adoptie.utilizator.UtilizatorDTO



@Composable
fun DetaliiAnuntScreen(
    anuntId: Long,
    onNavigateToProfile: (Long) -> Unit,
    onBack: () -> Unit
) {
    var detaliiState by remember { mutableStateOf<DetaliiState>(DetaliiState.Loading) }

    LaunchedEffect(anuntId) {
        detaliiState = try {
            val anunt = RetrofitClient.anuntService.getAnuntDetails(anuntId)
            val user = RetrofitClient.utilizatorService.getUtilizatorDetails(anunt.utilizatorId)
            val localitate = RetrofitClient.localitateService.getLocalitateDetails(anunt.locatieId)

            DetaliiState.Success(AnuntDetails(anunt, user, localitate))
        } catch (e: Exception){
            DetaliiState.Error("Nu s-au putut incarca detaliile ${e.message}")
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detalii anunț") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Înapoi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
            when(val state = detaliiState){
                is DetaliiState.Loading -> Box(Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is DetaliiState.Error -> Box(Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
                is DetaliiState.Success ->{
                    DetaliiContent(
                        anunt = state.details.anunt,
                        user = state.details.user,
                        localitate = state.details.localitate,
                        onNavigateToProfile = onNavigateToProfile,
                        modifier = Modifier.padding(innerPadding),
                        onBack = onBack
                    )
                }
            }
    }
}

@Composable
fun DetaliiContent(
    anunt: AnuntDTO,
    user: UtilizatorDTO? = null,
    localitate: LocalitateDTO? = null,
    onNavigateToProfile: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onBack: () -> Unit) {
    val context = LocalContext.current

    val displayLat = anunt.latitudine ?: localitate?.lat ?: 0.0
    val displayLng = anunt.longitudine ?: localitate?.lng ?: 0.0
    val numeLocatie = if (anunt.categorie != Categorie.ADOPTIE)
        "${anunt.categorie.display}: ${anunt.titlu}"
    else
        localitate?.diacritice ?: ""

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        item {
            ImageCarousel(imageUrls = anunt.listaImagini)
            Spacer(Modifier.height(16.dp))

            CategoryChip(categorie = anunt.categorie)
            Spacer(Modifier.height(12.dp))

            Text(
                text = anunt.titlu,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = anunt.descriere,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            if (anunt.categorie != Categorie.PROBLEMA) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Specie", anunt.specie)
                        DetailRow("Rasă", anunt.rasa)
                        DetailRow("Gen", anunt.gen.name.lowercase().replaceFirstChar { it.uppercase() })
                        DetailRow("Vârstă", anunt.varsta.display)
                    }
                }
            }

            if (isEditable) {
                Spacer(Modifier.height(8.dp))
                DetailRow("Stare", anunt.stare.name.lowercase())
            }
            Spacer(Modifier.height(20.dp))
            Text("Locație", style = MaterialTheme.typography.titleMedium)
            LocatieWidget(
                latitudine = displayLat,
                longitudine = displayLng,
                numeLocatie = numeLocatie
            ) {
                // Deschide Google Maps extern pentru navigare
                val gmmIntentUri = Uri.parse("geo:$displayLat,$displayLng?q=$displayLat,$displayLng($numeLocatie)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                try {
                    context.startActivity(mapIntent)
                } catch (e: Exception) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    context.startActivity(browserIntent)
                }
            }
        }
        if (!isEditable && user != null && localitate != null && onNavigateToProfile != null) {
            item {
                UtilizatorCard(
                    user = user,
                    localitate = localitate,
                    onCardClick = { onNavigateToProfile(user.id) }
                )
            }
        }

    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}