@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            text = "Detalii Anunt",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )
    }
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        item{
            ImageCarousel(imageUrls = anunt.listaImagini)
            Spacer(Modifier.height(16.dp))
            Text(anunt.titlu, fontFamily = FontFamily.SansSerif, fontSize = 26.sp, fontWeight = FontWeight.W400)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            Text(anunt.descriere,fontFamily = FontFamily.SansSerif, fontSize = 18.sp, fontWeight = FontWeight.Normal )
            Spacer(Modifier.height(8.dp))
        }
        item {
            Text("Specie: ${anunt.specie}", style = MaterialTheme.typography.bodyLarge)
            Text("Rasă: ${anunt.rasa}", style = MaterialTheme.typography.bodyLarge)
            Text("Gen: ${anunt.gen.name.lowercase()}", style = MaterialTheme.typography.bodyLarge)
            Text("Vârstă: ${anunt.varsta.display}", style = MaterialTheme.typography.bodyLarge)
            if(isEditable){
                Text("Stare: ${anunt.stare}", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(16.dp))
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