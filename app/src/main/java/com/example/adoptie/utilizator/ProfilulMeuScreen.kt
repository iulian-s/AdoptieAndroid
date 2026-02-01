package com.example.adoptie.utilizator

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adoptie.BASE_URL
import com.example.adoptie.RetrofitClient
import com.example.adoptie.localitate.LocalitateDTO
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilulMeuScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    var userState by remember { mutableStateOf<UtilizatorDTO?>(null) }
    var localitateState by remember { mutableStateOf<LocalitateDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }

    // Stări pentru editare
    var editNume by remember { mutableStateOf("") }
    var editTelefon by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var parolaVecheInput by remember { mutableStateOf("") }
    var parolaNouaInput by remember { mutableStateOf("") }
    var vreaSaSchimbeParola by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var parolaConfirmareInput by remember { mutableStateOf("") }
    var isDeleting by remember { mutableStateOf(false) }

    // Launcher pentru alegerea pozei din galerie
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.utilizatorService.getInfoUtilizator()
            if (response.isSuccessful) {
                val user = response.body()
                userState = user
                editNume = user?.nume ?: ""
                editTelefon = user?.telefon ?: ""
                //localitateState = RetrofitClient.localitateService.getLocalitateDetails(user?.localitateId)

            }
        } catch (e: Exception) { /* Log eroare */ }
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profilul meu") },
                navigationIcon = {
                    IconButton(onClick = if (isEditing) { { isEditing = false } } else onBack) {
                        Icon(if (isEditing) Icons.Default.Close else Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            scope.launch {
                                // 1. Creăm JSON-ul pentru DTO
                                val editDto = EditareUtilizatorDTO(
                                    nume = editNume,
                                    telefon = editTelefon,
                                    //localitateId = localitateState?.id ?: 1, // Nu trimite 0, trimite un ID valid
                                    parolaVeche = if (parolaVecheInput.isNotBlank()) parolaVecheInput else null,
                                    parolaNoua = if (parolaNouaInput.isNotBlank()) parolaNouaInput else null
                                )

                                val dtoJson = Gson().toJson(editDto)
                                val dtoPart = dtoJson.toRequestBody("application/json".toMediaTypeOrNull())

                                // 2. Pregătim imaginea dacă a fost selectată una nouă
                                val avatarPart = selectedImageUri?.let { uri ->
                                    val file = context.createTempFileFromUri(uri)
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    // "avatar" trebuie să se potrivească cu @RequestPart("avatar") din Spring
                                    MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                                }

                                try {
                                    val response = RetrofitClient.utilizatorService.editareProfil(dtoPart, avatarPart)
                                    if (response.isSuccessful) {
                                        userState = response.body()
                                        isEditing = false
                                        Toast.makeText(context, "Profil actualizat!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Eroare la salvare", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Salvează", tint = Color(0xFF4CAF50))
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editează")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            userState?.let { user ->
                Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {

                    // Secțiune Avatar
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        val avatarUrl = BASE_URL + (user.avatar ?: "/imagini/avatar.png")
                        AsyncImage(
                            model = selectedImageUri ?: avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        if (isEditing) {
                            FilledIconButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.align(Alignment.BottomEnd).size(32.dp)
                            ) {
                                Icon(Icons.Default.Face, null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editNume,
                            onValueChange = { editNume = it },
                            label = { Text("Nume Complet") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = editTelefon,
                            onValueChange = { editTelefon = it },
                            label = { Text("Telefon") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { vreaSaSchimbeParola = !vreaSaSchimbeParola },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (vreaSaSchimbeParola) "Renunță la schimbarea parolei" else "Schimbă parola")
                        }

                        if (vreaSaSchimbeParola) {
                            Spacer(Modifier.height(8.dp))

                            // Câmp pentru Parola Veche (Userul o scrie manual)
                            OutlinedTextField(
                                value = parolaVecheInput,
                                onValueChange = { parolaVecheInput = it },
                                label = { Text("Parola actuală") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(), // Ascunde caracterele
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            Spacer(Modifier.height(8.dp))

                            // Câmp pentru Parola Nouă
                            OutlinedTextField(
                                value = parolaNouaInput,
                                onValueChange = { parolaNouaInput = it },
                                label = { Text("Parola nouă") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                        }
                        // Locația o lăsăm momentan Read-Only sau adaugi Dropdown similar cu Anunțurile
                        //InfoRow(label = "Locație (ne-editabilă)", value = "${localitateState?.nume}")
                    } else {
                        InfoRow(label = "Nume", value = user.nume)
                        InfoRow(label = "Email", value = user.username)
                        InfoRow(label = "Telefon", value = user.telefon)
                    }

                    // Butonul de ștergere (afișat doar când nu edităm)
                    if (!isEditing) {
                        Spacer(Modifier.height(32.dp))
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Șterge Contul")
                        }
                    }

// Dialogul de confirmare
                    if (showDeleteDialog) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = {
                                showDeleteDialog = false
                                parolaConfirmareInput = ""
                            },
                            title = { Text("Ești sigur?") },
                            text = {
                                Column {
                                    Text("Această acțiune este definitivă. Introdu parola pentru a confirma ștergerea contului:")
                                    Spacer(Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = parolaConfirmareInput,
                                        onValueChange = { parolaConfirmareInput = it },
                                        label = { Text("Parola") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            },
                            confirmButton = {
                                androidx.compose.material3.Button(
                                    onClick = {
                                        if (parolaConfirmareInput.isNotBlank()) {
                                            scope.launch {
                                                isDeleting = true
                                                try {
                                                    // Transmitem parola ca query parameter conform controllerului
                                                    val response = RetrofitClient.utilizatorService.stergereContUtilizator(parolaConfirmareInput)
                                                    if (response.code() == 200 || response.isSuccessful) {
                                                        Toast.makeText(context, "Cont șters.", Toast.LENGTH_SHORT).show()
                                                        onAccountDeleted()

                                                        //onBack() // Sau navigare către Login
                                                    } else {
                                                        Toast.makeText(context, "Parolă incorectă!", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
//                                                    if (e.message?.contains("lenient") == true) {
//                                                        onAccountDeleted()
//                                                    }
                                                    Toast.makeText(context, "Serverul a intampinat o eroare...", Toast.LENGTH_SHORT).show()
                                                } finally {
                                                    isDeleting = false
                                                    showDeleteDialog = false
                                                    parolaConfirmareInput = ""
                                                }
                                            }
                                        }
                                    },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    if (isDeleting) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
                                    else Text("Șterge definitiv", color = Color.White)
                                }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Anulează")
                                }
                            }
                        )
                    }
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

fun Context.createTempFileFromUri(uri: Uri): File {
    val inputStream = contentResolver.openInputStream(uri)
    val file = File(cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { inputStream?.copyTo(it) }
    return file
}