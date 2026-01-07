package com.example.adoptie

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adoptie.auth.AuthApiService
import com.example.adoptie.auth.TokenManager
import kotlinx.coroutines.launch

sealed class SetariRoutes(val route: String){
    object Main : SetariRoutes("setari_main")
    object Login : SetariRoutes("login")
    object Register : SetariRoutes("register")
}
@Composable
fun SetariScreen() {
    val setariNavController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    // Stare pentru a verifica dacă utilizatorul este logat
    var isLoggedIn by remember {
        mutableStateOf(tokenManager.getToken() != null)
    }

    NavHost(
        navController = setariNavController,
        startDestination = SetariRoutes.Main.route
    ) {
        // Pagina principală de Setări
        composable(SetariRoutes.Main.route) {
            SetariMainContent(
                isLoggedIn = isLoggedIn,
                onNavigateToLogin = { setariNavController.navigate(SetariRoutes.Login.route) },
                onLogout = {
                    tokenManager.deleteToken()
                    isLoggedIn = false // Actualizăm starea pentru a schimba UI-ul instant
                }
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
    }

}

@Composable
fun SetariMainContent(
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isLoggedIn) {
            // UI pentru utilizator LOGAT
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Contul tău", style = MaterialTheme.typography.titleMedium)
                    Text("Ești conectat în aplicație.", style = MaterialTheme.typography.bodyMedium)
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
            // UI pentru utilizator ANONIM
            Text(
                text = "Conectează-te pentru a putea posta anunțuri și a comunica cu alți utilizatori.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Intră în cont")
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

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Înapoi")
        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Text("Bine ai revenit!", style = MaterialTheme.typography.headlineLarge)
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
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
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
                enabled = nume.isNotBlank() && email.isNotBlank() && parola == confirmaParola  && email.contains("@")// Validare de bază
            ) {
                Text("Înregistrează-te")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}