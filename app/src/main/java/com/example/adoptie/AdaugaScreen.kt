package com.example.adoptie

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.anunt.CreareAnuntDTO
import com.example.adoptie.anunt.Gen
import com.example.adoptie.anunt.Stare
import com.example.adoptie.anunt.Varsta
import com.example.adoptie.auth.TokenManager
import com.example.adoptie.localitate.LocalitateDTO
import com.example.adoptie.utilizator.createTempFileFromUri
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.collections.firstOrNull


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaugaScreen(onSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    var isCheckingAuth by remember { mutableStateOf(true) }
    val isLoggedIn = remember { tokenManager.getToken() != null }
    // State-uri pentru formular
    var titlu by remember { mutableStateOf("") }
    var descriere by remember { mutableStateOf("") }
    var specie by remember { mutableStateOf("") }
    var rasa by remember { mutableStateOf("") }
    var gen by remember { mutableStateOf(Gen.MASCUL) }
    var varsta by remember { mutableStateOf(Varsta.NECUNOSCUT) }
    var locatieId by remember { mutableLongStateOf(0L) }

    var imaginiUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }

    //pt dropdownuri
    var raseMap by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var expandedSpecie by remember { mutableStateOf(false) }
    var expandedRasa by remember { mutableStateOf(false) }
    var expandedGen by remember { mutableStateOf(false) }
    var expandedVarsta by remember { mutableStateOf(false) }
    var expandedJudet by remember { mutableStateOf(false) }
    var expandedLocalitate by remember { mutableStateOf(false) }

    // Liste de locatii
    var listaJudete by remember { mutableStateOf<List<String>>(emptyList()) }
    var listaOraseByJudet by remember { mutableStateOf<List<LocalitateDTO>>(emptyList()) }

    var judet by remember { mutableStateOf<String?>("") }
    var localitate by remember { mutableStateOf<String?>("") }




    val scope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }
    fun resetForm() {
        titlu = ""
        descriere = ""
        specie = ""
        rasa = ""
        imaginiUris = emptyList()
        locatieId = 0L
    }

    LaunchedEffect(Unit) {
        if (!isLoggedIn) {
            onNavigateToLogin()
        } else {
            isCheckingAuth = false
        }
    }

    if (isCheckingAuth || !isLoggedIn) {
        // Un ecran gol sau un loader până când se face redirecționarea
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }


    LaunchedEffect(Unit) {
        listaJudete = RetrofitClient.localitateService.getJudete()
        try {
            // Încarcă speciile și rasele de la backend
            val response = RetrofitClient.animaluteService.getRase()
            raseMap = response
        } catch (e: Exception) { e.printStackTrace() }
    }
    LaunchedEffect(judet) {
        if (judet != null) {
            try {
                listaOraseByJudet = RetrofitClient.localitateService.getByJudet(judet!!)
            } catch (e: Exception) { /* Log eroare */
            }
        } else {
            listaOraseByJudet = emptyList()
        }
    }

    // Picker pentru imagini multiple
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris -> imaginiUris = uris }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Adaugă Anunț Nou") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Secțiune Selectare Imagini
            Button(
                onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Selectează Imagini (${imaginiUris.size})")
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(imaginiUris) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Câmpuri Text
            OutlinedTextField(
                value = titlu,
                onValueChange = { titlu = it },
                label = { Text("Nume") },
                modifier = Modifier.fillMaxWidth(),
                isError = titlu.isBlank(),
//                supportingText = {
//                    if (titlu.isBlank()) Text("Camp obligatoriu", color = Color.Red)
//                }
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = descriere,
                onValueChange = { descriere = it },
                label = { Text("Descriere") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = descriere.isBlank(),
//                supportingText = {
//                    if (descriere.isBlank()) Text("Camp obligatoriu", color = Color.Red)
//                }
            )

            Spacer(Modifier.height(16.dp))
            // 1. Dropdown SPECIE
            EditDropdown(
                label = "Specie",
                selectedValue = specie,
                options = raseMap.keys.toList(),
                optionToString = { it },
                onValueChange = {
                    specie = it
                    rasa = raseMap[it]?.firstOrNull() ?: "" // Resetăm rasa la prima disponibilă din noua specie
                },
                expanded = expandedSpecie,
                onExpandedChange = { expandedSpecie = it }
            )

            // 2. Dropdown RASĂ (depinde de Specie)
            EditDropdown(
                label = "Rasă",
                selectedValue = rasa,
                options = raseMap[specie] ?: emptyList(),
                optionToString = { it },
                onValueChange = { rasa = it },
                expanded = expandedRasa,
                onExpandedChange = { expandedRasa = it }
            )

            // 3. Dropdown GEN (Enum)
            EditDropdown(
                label = "Gen",
                selectedValue = gen.name.lowercase(),
                options = Gen.entries, // Presupunând că ai enum-ul Gen cu entries (Kotlin 1.9+)
                optionToString = { it.name.lowercase() },
                onValueChange = { gen = it },
                expanded = expandedGen,
                onExpandedChange = { expandedGen = it }
            )

            // 4. Dropdown VÂRSTĂ (Enum)
            EditDropdown(
                label = "Vârstă",
                selectedValue = varsta.display,
                options = Varsta.entries,
                optionToString = { it.display },
                onValueChange = { varsta = it },
                expanded = expandedVarsta,
                onExpandedChange = { expandedVarsta = it }
            )
            //6. Dropdown Judet
            EditDropdown(
                label = "Judet",
                selectedValue = judet ?: "",
                options = listaJudete,
                optionToString = { it },
                onValueChange = {
                    judet = it
                    localitate = ""
                },
                expanded = expandedJudet,
                onExpandedChange = { expandedJudet = it }
            )
            //6. Dropdown Localitate
            if(judet != null) {
                EditDropdown(
                    label = "Localitate",
                    selectedValue = localitate ?: "",
                    options = listaOraseByJudet,
                    optionToString = { it.nume },
                    onValueChange = {
                        localitate = it.nume
                        locatieId = it.id
                    },
                    expanded = expandedLocalitate,
                    onExpandedChange = { expandedLocalitate = it }
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (imaginiUris.isEmpty()) {
                        Toast.makeText(context, "Adauga cel putin o imagine!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        isSubmitting = true
                        try {

                            // 1. Pregătim DTO-ul conform specificațiilor backend
                            val dto = CreareAnuntDTO(
                                titlu = titlu,
                                descriere = descriere,
                                specie = specie,
                                rasa = rasa,
                                gen = gen,
                                varsta = varsta,
                                locatieId = locatieId
                            )

                            val dtoJson = Gson().toJson(dto)
                            val dtoPart = dtoJson.toRequestBody("application/json".toMediaTypeOrNull())

                            // 2. Pregătim imaginile
                            val imaginiParts = imaginiUris.map { uri ->
                                val file = context.createTempFileFromUri(uri)
                                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                MultipartBody.Part.createFormData("imagini", file.name, requestFile)
                            }

                            val response = RetrofitClient.anuntService.creareAnunt(dtoPart, imaginiParts)

                            if (response.isSuccessful) {
                                showSuccessDialog = true
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Eroare: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) CircularProgressIndicator(color = Color.White)
                else Text("Publică Anunțul")
            }

            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { /* Nu permitem închiderea prin click afară pentru a asigura fluxul */ },
                    text = { Text("Anunțul a fost inregistrat cu succes!") },

                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                resetForm() // Curățăm câmpurile
                                onSuccess() // Trimitem utilizatorul la prima pagină
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
