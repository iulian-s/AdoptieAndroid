package com.example.adoptie

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.adoptie.anunt.AnuntDTO
import com.example.adoptie.anunt.Gen
import com.example.adoptie.anunt.ImageCarousel
import com.example.adoptie.anunt.Stare
import com.example.adoptie.anunt.Varsta
import com.example.adoptie.auth.AuthApiService
import com.example.adoptie.auth.TokenManager
import com.example.adoptie.localitate.LocalitateDTO
import com.example.adoptie.utilizator.ProfilulMeuScreen
import com.example.adoptie.utilizator.createTempFileFromUri
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.text.lowercase

sealed class SetariRoutes(val route: String){
    object Main : SetariRoutes("setari_main")
    object Login : SetariRoutes("login")
    object Register : SetariRoutes("register")
    object ProfilulMeu : SetariRoutes("profilul_meu")
    object AnunturileMele : SetariRoutes("anunturile_mele")

    object DetaliiAnuntPropriu : SetariRoutes("detalii_anunt_propriu/{anuntId}") {
        fun createRoute(id: Long) = "detalii_anunt_propriu/$id"
    }
}
@Composable
fun SetariScreen(onNavigateToGlobalDetail: (Long) -> Unit,
                 onProfileNavControllerReady: (NavHostController) -> Unit) {
    val setariNavController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    // Stare pentru a verifica dacă utilizatorul este logat
    var isLoggedIn by remember {
        mutableStateOf(tokenManager.getToken() != null)
    }

    LaunchedEffect(Unit) {
        onProfileNavControllerReady(setariNavController)
    }

    NavHost(
        navController = setariNavController,
        startDestination = SetariRoutes.Main.route
    ) {
        // Pagina principală de Setări
        composable(SetariRoutes.Main.route) {
            LaunchedEffect(isLoggedIn) {
                if (!isLoggedIn) {
                    // Putem lăsa utilizatorul pe pagina de "Intră în cont"
                    // sau să îl trimitem automat la ecranul de Login
                     setariNavController.navigate(SetariRoutes.Login.route)
                }
            }
            SetariMainContent(
                isLoggedIn = isLoggedIn,
                onNavigateToLogin = { setariNavController.navigate(SetariRoutes.Login.route) },
                onLogout = {
                    isLoggedIn = false // Actualizăm starea pentru a schimba UI-ul instant
                    tokenManager.deleteToken()

                },
                onNavigateToProfil = { setariNavController.navigate(SetariRoutes.ProfilulMeu.route) },
                onNavigateToAnunturi = { setariNavController.navigate(SetariRoutes.AnunturileMele.route) },
            )
        }

        // Pagina de Login
        composable(SetariRoutes.Login.route) {
            LoginScreen(
                navController = setariNavController,
                onNavigateToRegister = { setariNavController.navigate(SetariRoutes.Register.route) },
                onBack = { setariNavController.popBackStack() },
                onLoginSuccess = {
                    isLoggedIn = true
                    // Curățăm stiva de navigare și mergem la "Main" sau un ecran de Profil
                    setariNavController.navigate(SetariRoutes.Main.route) {
                        popUpTo(SetariRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pagina de Register
        composable(SetariRoutes.Register.route) {
            RegisterScreen(
                onBack = { setariNavController.popBackStack() },
                onRegisterSuccess = {
                    isLoggedIn = true
                    setariNavController.navigate(SetariRoutes.Main.route) {
                        popUpTo(SetariRoutes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(SetariRoutes.ProfilulMeu.route) {
            ProfilulMeuScreen(
                onBack = { setariNavController.popBackStack() },
                onAccountDeleted = {
                    isLoggedIn = false
                    tokenManager.deleteToken()
                    setariNavController.navigate(SetariRoutes.Main.route){
                        popUpTo(0){inclusive = true}
                    }
                }
            )
        }



        composable(SetariRoutes.AnunturileMele.route) {
            AnunturileMeleScreen(
                onBack = { setariNavController.popBackStack() },
                onNavigateToDetail = { id ->
                    setariNavController.navigate(SetariRoutes.DetaliiAnuntPropriu.createRoute(id))
                }
            )
        }

        composable(
            route = SetariRoutes.DetaliiAnuntPropriu.route,
            arguments = listOf(navArgument("anuntId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("anuntId") ?: 0L
            // Aici chemăm noul ecran pe care îl vom crea
            AnuntPropriuDetaliiScreen(
                anuntId = id,
                onBack = { setariNavController.popBackStack() }
            )
        }
    }

}

@Composable
fun SetariMainContent(
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfil: () -> Unit,
    onNavigateToAnunturi: () -> Unit,

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        if (isLoggedIn) {
            Text(
                text = "Profil",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            // UI pentru utilizator LOGAT
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                onClick = onNavigateToProfil
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Text("Profilul meu", style = MaterialTheme.typography.titleMedium)
                }
            }

            Card(
                onClick = onNavigateToAnunturi,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Text("Anunțurile mele", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Deconectare")
            }
        } else {
//            // UI pentru utilizator ANONIM
//            Text(
//                text = "Conectează-te pentru a putea posta anunțuri și a comunica cu alți utilizatori.",
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.bodyLarge
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Button(
//                onClick = onNavigateToLogin,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Intră în cont")
//            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onBack: () -> Unit, navController: NavController, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    var username by remember { mutableStateOf("") }
    var parola by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
//        IconButton(onClick = onBack) {
//            Icon(Icons.Default.ArrowBack, contentDescription = "Înapoi")
//        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Text("Bine ai venit!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { newValue ->
                // Filtrăm manual orice caracter de tip "new line" sau "tab"
                // în caz că utilizatorul dă paste la un text care le conține
                if (!newValue.contains("\n") && !newValue.contains("\t") && !newValue.contains(" ")) {
                    username = newValue
                }
            },

            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next // Trimite focusul la următorul câmp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = parola,
            onValueChange = { newValue ->
                if (!newValue.contains("\n") && !newValue.contains("\t") && !newValue.contains(" ")) {
                    parola = newValue
                }
            },
            label = { Text("Parolă") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done // Schimbă Enter în "Gata/Bifat"
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Aici poți ascunde tastatura sau declanșa direct logarea
                    defaultKeyboardAction(ImeAction.Done)
                }
            )
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitClient.authService.login(
                            AuthApiService.AuthRequest(username, parola)
                        )
                        if (response.isSuccessful) {
                            response.body()?.token?.let { token ->
                                tokenManager.saveToken(token)
                                onLoginSuccess() // Navighează înapoi sau către profil
                            }
                        } else {
                            errorMessage = "Eroare: Username sau parolă incorectă"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Eroare de conexiune la server"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Conectare")
        }

        TextButton(onClick = onNavigateToRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Nu ai cont? Înregistrează-te")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onRegisterSuccess: () -> Unit) {
    // Stări pentru câmpurile de input
    var nume by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefon by remember { mutableStateOf("") }
    var parola by remember { mutableStateOf("") }
    var confirmaParola by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creează Cont") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Înapoi")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Alătură-te comunității noastre pentru a adopta sau posta anunțuri.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Câmp Nume Complet
            OutlinedTextField(
                value = nume,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n") && !newValue.contains("\t")) {
                        nume = newValue
                    }
                },
                label = { Text("Nume Complet") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Câmp Email
            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n") && !newValue.contains("\t") && !newValue.contains(" ")) {
                        email = newValue
                    }
                },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Câmp Telefon
            OutlinedTextField(
                value = telefon,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n") && !newValue.contains("\t") && !newValue.contains(" ")) {
                        telefon = newValue
                    }
                },
                label = { Text("Număr Telefon") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Câmp Parolă
            OutlinedTextField(
                value = parola,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n") && !newValue.contains("\t")&& !newValue.contains(" ")) {
                        parola = newValue
                    }
                },
                label = { Text("Parolă") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmare Parolă
            OutlinedTextField(
                value = confirmaParola,
                onValueChange = { confirmaParola = it
                },
                label = { Text("Confirmă Parola") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = parola != confirmaParola && confirmaParola.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(32.dp))


            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            // Buton Înregistrare
            Button(
                onClick = {
                    scope.launch {
                        // 1. Definim DTO-ul clar înainte
                        val dto = AuthApiService.CreareUtilizatorDTO(
                            nume = nume,
                            username = email,
                            email = email,
                            parola = parola,
                            telefon = telefon

                        )

                        try {
                            // 2. Apelăm API-ul (compilatorul va știi acum că rezultatul este Response<AuthResponse>)
                            val response = RetrofitClient.authService.register(dto)

                            if (response.isSuccessful) {
                                val body = response.body()
                                body?.token?.let { token ->
                                    tokenManager.saveToken(token)
                                    Toast.makeText(context, "Bine ai venit, $nume!", Toast.LENGTH_LONG).show()
                                    onRegisterSuccess() // Folosește callback-ul tău de succes
                                }
                            } else {
                                // Gestionare eroare (ex: email luat)
                                if (response.code() == 409 || response.code() == 400) {
                                    errorMessage = "Acest email este deja asociat unui cont."
                                } else {
                                    errorMessage = "Eroare la înregistrare. Încearcă din nou."
                                }
                            }
                        } catch (e: Exception) {
                            // Gestionare eroare (ex: Toast sau stare de eroare)
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nume.isNotBlank() && email.isNotBlank() && parola == confirmaParola  && email.contains("@") && parola.isNotBlank()// Validare de bază
            ) {
                Text("Înregistrează-te")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnunturileMeleScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit
) {
    var anunturi by remember { mutableStateOf<List<AnuntDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.utilizatorService.getAnunturiProprii()
            if (response.isSuccessful) {
                val listaAnunturi = response.body() ?: emptyList()
                anunturi = listaAnunturi.sortedWith(
                    compareBy<AnuntDTO> { it.stare}
                        .thenByDescending{it.updatedAt}
                )
            }
        } catch (e: Exception) { e.printStackTrace() }
        isLoading = false
    }

    Scaffold() { padding ->
        if (isLoading) {
            // Indicator încărcare
        } else if (anunturi.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nu ai postat niciun anunț încă.")
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
                    text = "Anunturile mele",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            LazyColumn(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
                items(anunturi) { anunt ->
                    AnuntPropriuItem(
                        anunt,
                        onDetailClick = { id ->
                            println("Click pe anunțul: $id")
                            onNavigateToDetail(id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnuntPropriuItem(
    anunt: AnuntDTO,
    onDetailClick: (Long) -> Unit
) {
    val imageUrl = if(anunt.listaImagini.isNotEmpty()){
        BASE_IMAGE_URL + anunt.listaImagini.first()
    }
    else{
        null
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onDetailClick(anunt.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🖼️ Imagine Mică (Thumbnail)
            Surface(
                modifier = Modifier.size(70.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (anunt.listaImagini.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 📝 Detalii Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anunt.titlu,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${anunt.specie} • ${anunt.rasa}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tag pentru Status (Activ/Inactiv)
                val statusColor = if (anunt.stare == Stare.ACTIV) Color(0xFF4CAF50) else Color.Gray
                //val statusText = if (anunt.stare == Stare.ACTIV) "Activ" else "Inactiv"
                val statusText = when(anunt.stare){
                    Stare.ACTIV -> "Activ"
                    Stare.INACTIV -> "Inactiv"
                    Stare.NEVERIFICAT -> "Neverificat"
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnuntPropriuDetaliiScreen(anuntId: Long, onBack: () -> Unit) {
    var anunt by remember { mutableStateOf<AnuntDTO?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var raseMap by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var expandedSpecie by remember { mutableStateOf(false) }
    var expandedRasa by remember { mutableStateOf(false) }
    var expandedGen by remember { mutableStateOf(false) }
    var expandedVarsta by remember { mutableStateOf(false) }
    var expandedStare by remember {mutableStateOf(false)}
    var expandedJudet by remember { mutableStateOf(false) }
    var expandedLocalitate by remember { mutableStateOf(false) }

    var editTitlu by remember { mutableStateOf("") }
    var editDescriere by remember { mutableStateOf("") }
    var editSpecie by remember { mutableStateOf("") }
    var editRasa by remember { mutableStateOf("") }
    var editGen by remember { mutableStateOf<Gen?>(null) }
    var editVarsta by remember { mutableStateOf<Varsta?>(null) }
    var editStare by remember { mutableStateOf<Stare?>(null) }
    var editJudet by remember { mutableStateOf<String?>("") }
    var editLocalitate by remember { mutableStateOf<String?>("") }
    var editLocalitateId by remember { mutableStateOf<Long?>(0) }

    // Liste de locatii
    var listaJudete by remember { mutableStateOf<List<String>>(emptyList()) }
    var listaOraseByJudet by remember { mutableStateOf<List<LocalitateDTO>>(emptyList()) }

    var localitateState by remember{ mutableStateOf<LocalitateDTO?>(null)}

    //imagini
    var imaginiNoiUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris -> imaginiNoiUris = uris }

    var showBackupWarning by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(anuntId) {
        try {
            val data = RetrofitClient.anuntService.getAnuntDetails(anuntId)
            anunt = data
            localitateState = RetrofitClient.localitateService.getLocalitateDetails(anunt?.locatieId)
            // Inițializăm câmpurile de editare cu datele primite
            editTitlu = data.titlu
            editDescriere = data.descriere
            editSpecie = data.specie
            editRasa = data.rasa
            editGen = data.gen
            editVarsta = data.varsta
            editStare = data.stare
            editJudet = localitateState?.judet
            editLocalitate = localitateState?.nume
            editLocalitateId = localitateState?.id
        } catch (e: Exception) { e.printStackTrace() }

        try {
            // Încarcă speciile și rasele de la backend
            val response = RetrofitClient.animaluteService.getRase()
            raseMap = response
        } catch (e: Exception) { e.printStackTrace() }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        listaJudete = RetrofitClient.localitateService.getJudete()
    }
    LaunchedEffect(editJudet) {
        if (editJudet != null) {
            try {
                listaOraseByJudet = RetrofitClient.localitateService.getByJudet(editJudet!!)
            } catch (e: Exception) { /* Log eroare */
            }
        } else {
            listaOraseByJudet = emptyList()
        }
    }

    val isDataValid = editTitlu.isNotBlank() &&
            editSpecie.isNotBlank() &&
            editRasa.isNotBlank() &&
            editJudet?.isNotBlank() == true &&
            editLocalitate?.isNotBlank() == true

    fun executaSalvareaAnuntului() {
        isSaving = true
        scope.launch {
            try {
                val updatedDto = anunt!!.copy(
                    titlu = editTitlu,
                    descriere = editDescriere,
                    specie = editSpecie,
                    rasa = editRasa,
                    gen = editGen!!,
                    varsta = editVarsta!!,
                    stare = editStare!!,
                    locatieId = editLocalitateId!!
                )

                val dtoPart = Gson().toJson(updatedDto).toRequestBody("application/json".toMediaTypeOrNull())
                val imaginiParts = imaginiNoiUris.map { uri ->
                    val file = context.createTempFileFromUri(uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("imagini", file.name, requestFile)
                }

                val response = RetrofitClient.anuntService.editareAnuntPropriu(
                    anuntId, dtoPart, imaginiParts.ifEmpty { null }
                )
                if (response.isSuccessful) {
                    anunt = response.body()
                    imaginiNoiUris = emptyList()
                    isEditing = false
                    Toast.makeText(context, "Anunt actualizat cu succes!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Eroare la actualizare, va rugam incercati mai tarziu!", Toast.LENGTH_SHORT).show()
                println(e.message)
            }
            finally {
                isSaving = false
            }
        }
    }

    Scaffold(){ padding ->
        if (isLoading) { /* CircularProgressIndicator */ }
        else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // BACK
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }

                // TITLE
                Text(
                    text = "Anunt",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )

                // EDIT / SAVE
                if(editStare != Stare.NEVERIFICAT){
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                if (imaginiNoiUris.isNotEmpty()) {
                                    showBackupWarning = true
                                } else {
                                    executaSalvareaAnuntului()
                                }
                            } else {
                                isEditing = true
                            }
                        },
                        modifier = Modifier.align(Alignment.TopEnd),
                        enabled = !isEditing || isDataValid
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Salvează" else "Editează",
                            tint = if (isEditing) Color(0xFF4CAF50) else LocalContentColor.current
                        )
                    }
                }

            }

        }

            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)) {

                ImageCarousel(imageUrls = anunt?.listaImagini ?: emptyList())
                Spacer(Modifier.height(16.dp))

                if (isEditing) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { multiplePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddCircle, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Adaugă imagini noi (${imaginiNoiUris.size})")
                    }

                    // Preview poze noi selectate
                    if (imaginiNoiUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(imaginiNoiUris) { uri ->
                                AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (isEditing) {

                    // Câmpuri de editare
                    OutlinedTextField(
                        value = editTitlu,
                        onValueChange = { editTitlu = it },
                        label = { Text("Nume") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = editTitlu.isBlank(),
                        supportingText = {
                            if (editTitlu.isBlank()) Text("Camp obligatoriu", color = Color.Red)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editDescriere,
                        onValueChange = { editDescriere = it },
                        label = { Text("Descriere") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        isError = editTitlu.isBlank(),
                        supportingText = {
                            if (editTitlu.isBlank()) Text("Camp obligatoriu", color = Color.Red)
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                    // 1. Dropdown SPECIE
                    EditDropdown(
                        label = "Specie",
                        selectedValue = editSpecie,
                        options = raseMap.keys.toList(),
                        optionToString = { it },
                        onValueChange = {
                            editSpecie = it
                            editRasa = raseMap[it]?.firstOrNull() ?: "" // Resetăm rasa la prima disponibilă din noua specie
                        },
                        expanded = expandedSpecie,
                        onExpandedChange = { expandedSpecie = it }
                    )

                    // 2. Dropdown RASĂ (depinde de Specie)
                    EditDropdown(
                        label = "Rasă",
                        selectedValue = editRasa,
                        options = raseMap[editSpecie] ?: emptyList(),
                        optionToString = { it },
                        onValueChange = { editRasa = it },
                        expanded = expandedRasa,
                        onExpandedChange = { expandedRasa = it }
                    )

                    // 3. Dropdown GEN (Enum)
                    EditDropdown(
                        label = "Gen",
                        selectedValue = editGen?.name?.lowercase() ?: "",
                        options = Gen.entries, // Presupunând că ai enum-ul Gen cu entries (Kotlin 1.9+)
                        optionToString = { it.name.lowercase() },
                        onValueChange = { editGen = it },
                        expanded = expandedGen,
                        onExpandedChange = { expandedGen = it }
                    )

                    // 4. Dropdown VÂRSTĂ (Enum)
                    EditDropdown(
                        label = "Vârstă",
                        selectedValue = editVarsta?.display ?: "",
                        options = Varsta.entries,
                        optionToString = { it.display },
                        onValueChange = { editVarsta = it },
                        expanded = expandedVarsta,
                        onExpandedChange = { expandedVarsta = it }
                    )
                    // 5. Dropdown Stare (Enum)

                    EditDropdown(
                        label = "Stare",
                        selectedValue = editStare?.name?.lowercase() ?: "",
                        options = listOf(Stare.ACTIV, Stare.INACTIV),
                        optionToString = { it.name.lowercase() },
                        onValueChange = { editStare = it },
                        expanded = expandedStare,
                        onExpandedChange = { expandedStare = it }
                    )


                    //6. Dropdown Judet
                    EditDropdown(
                        label = "Judet",
                        selectedValue = editJudet ?: "",
                        options = listaJudete,
                        optionToString = { it },
                        onValueChange = {
                            editJudet = it
                            editLocalitate = ""
                                        },
                        expanded = expandedJudet,
                        onExpandedChange = { expandedJudet = it }
                    )
                    //6. Dropdown Localitate
                    if(editJudet != null) {
                        EditDropdown(
                            label = "Localitate",
                            selectedValue = editLocalitate ?: "",
                            options = listaOraseByJudet,
                            optionToString = { it.nume },
                            onValueChange = {
                                editLocalitate = it.nume
                                editLocalitateId = it.id
                                            },
                            expanded = expandedLocalitate,
                            onExpandedChange = { expandedLocalitate = it }
                        )
                    }



                } else {
                    // Vizualizare normală (Reutilizăm stilul tău)
                    Text(anunt?.titlu ?: "", fontSize = 26.sp, fontWeight = FontWeight.W400)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text(anunt?.descriere ?: "", fontSize = 18.sp)
                    // Restul detaliilor (Specie, Rasă etc.) rămân sub formă de Text momentan
                    Spacer(Modifier.height(16.dp))
                    Text("Specie: ${anunt?.specie}", style = MaterialTheme.typography.bodyLarge)
                    Text("Rasă: ${anunt?.rasa}", style = MaterialTheme.typography.bodyLarge)
                    Text("Gen: ${anunt?.gen?.name?.lowercase()?.capitalize()}", style = MaterialTheme.typography.bodyLarge)
                    Text("Varsta: ${anunt?.varsta?.display}", style = MaterialTheme.typography.bodyLarge)
                    Text("Stare: ${anunt?.stare?.name?.lowercase()?.capitalize()}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Locatie: ${editLocalitate}, ${editJudet}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }


            }
        if (isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clickable(enabled = false) { }, // Blocăm click-urile
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Se actualizeaza informatiile...", color = Color.Black)
                }
            }
        }
        if (showBackupWarning) {
            AlertDialog(
                onDismissRequest = { showBackupWarning = false },
                title = { Text(text = "Atenție!") },
                text = {
                    Text(text = "Modificarea va înlocui complet imaginile actuale. Vă rugăm să faceți backup dacă nu le mai aveți stocate în altă parte.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBackupWarning = false
                            executaSalvareaAnuntului() // Utilizatorul a confirmat, pornim salvarea
                        }
                    ) {
                        Text("Am înțeles, salvează")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showBackupWarning = false }) {
                        Text("Anulează")
                    }
                }
            )
        }


        }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EditDropdown(
    label: String,
    selectedValue: String,
    options: List<T>,
    optionToString: (T) -> String,
    onValueChange: (T) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            isError = selectedValue.isBlank()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionToString(option)) },
                    onClick = {
                        onValueChange(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}



fun resetProfileStack(navController: NavHostController){
    navController.popBackStack(
        route = SetariRoutes.Main.route,
        inclusive = false
    )
}
