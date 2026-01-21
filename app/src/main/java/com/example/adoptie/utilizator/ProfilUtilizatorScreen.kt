package com.example.adoptie.utilizator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_URL
import com.example.adoptie.RetrofitClient
import com.example.adoptie.anunt.AnuntCard
import com.example.adoptie.anunt.Stare
import com.example.adoptie.localitate.LocalitateDTO

@Composable
fun ProfilUtilizatorScreen(
    userId: Long,
    onNavigateToDetails: (Long) -> Unit,
    onBack: () -> Unit
) {
    var profilState by remember {
        mutableStateOf<ProfilState>(ProfilState.Loading)
    }

    var localitateState by remember{ mutableStateOf<LocalitateDTO?>(null)}

    LaunchedEffect(userId) {
        profilState = try {
            val user = RetrofitClient.utilizatorService.getUtilizatorDetails(userId)
            //localitateState = RetrofitClient.localitateService.getLocalitateDetails(user.localitateId)
            val anunturi = user.anuntIds.mapNotNull { anuntId ->
                try{
                    val anunt = RetrofitClient.anuntService.getAnuntDetails(anuntId)
                    if(anunt.stare == Stare.ACTIV){
                        anunt
                    } else {null}
                } catch (e: Exception){
                    null
                }
            }
            ProfilState.Success(
                ProfilDetails(
                    user = user,
                    userAnunturi = anunturi
                )
            )
        } catch (e: Exception){
            ProfilState.Error("Nu s-a putut incarca profilul utilizatorului: ${e.message}")
        }
    }
    Scaffold  { innerPadding ->
        when (val state = profilState) {
            is ProfilState.Loading -> Box(Modifier
                .padding(innerPadding)
                .fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ProfilState.Error -> Box(Modifier
                .padding(innerPadding)
                .fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is ProfilState.Success -> ProfilContent(
                state.details,
                onNavigateToDetails = onNavigateToDetails,
                Modifier.padding(innerPadding),
                localitate = localitateState,
                onBack = onBack
            )
        }
    }
}

@Composable
fun ProfilContent(details: ProfilDetails, onNavigateToDetails: (Long) -> Unit, modifier: Modifier = Modifier, localitate: LocalitateDTO?, onBack: () -> Unit) {
    val user = details.user
    val context = LocalContext.current
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
            text = "Detaliile utilizatorului ${user.nume}",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )
    }
    LazyColumn (
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)
    ){
        item {
            Column (
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                val avatarUrl = BASE_URL+ (user.avatar ?: "/imagini/avatar.png")
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar ${user.username}",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                Text(user.nume.ifEmpty { user.username }, style = MaterialTheme.typography.headlineMedium)
                Row(
                    modifier = Modifier
                        .clickable{
                            initiatePhoneCall(context, user.telefon)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Apel",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(user.telefon, style = MaterialTheme.typography.bodyMedium)
                }

//                Text(
//                    text = "${localitate?.nume}, ${localitate?.judet}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            Text("Anunțuri Active (${details.userAnunturi.size})", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
        }
        items(details.userAnunturi){ anunt ->
            AnuntCard(anunt = anunt, onCardClick = {onNavigateToDetails(anunt.id)})
            Spacer(Modifier.height(8.dp))
        }
    }
}