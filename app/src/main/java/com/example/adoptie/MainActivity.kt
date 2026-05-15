package com.example.adoptie

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.adoptie.anunt.AnunturiRoutes
import com.example.adoptie.anunt.ExploreazaScreen
import com.example.adoptie.anunt.resetAnunturiStack
import com.example.adoptie.auth.TokenManager
import com.example.adoptie.ui.theme.AdoptieTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvents {
    private val _logoutEvent = MutableSharedFlow<Unit>(replay = 0)
    val logoutEvent = _logoutEvent.asSharedFlow()

    suspend fun triggerLogout() {
        _logoutEvent.emit(Unit)
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            AdoptieTheme {
                val items = listOf(
                    NavigationItem(
                        "Explorează",
                        Icons.Filled.Home,
                        Icons.Outlined.Home
                    ),
                    NavigationItem(
                        "Adaugă",
                        Icons.Filled.AddCircle,
                        Icons.Outlined.AddCircle
                    ),
                    NavigationItem(
                        "Profil",
                        Icons.Filled.Person,
                        Icons.Outlined.Person
                    )
                )
                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }

                var anunturiNavController: NavHostController? by remember {
                    mutableStateOf(null)
                }
                var profileNavController: NavHostController? by remember {
                    mutableStateOf(null)
                }
                var setariNavHostController by remember { mutableStateOf<NavHostController?>(null) }
                val context = LocalContext.current



                Scaffold(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    bottomBar = {
                        NavigationBar(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            items.forEachIndexed { index, item ->
                                val selected = selectedItemIndex == index
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        if (index == 0 && selectedItemIndex == 0) {
                                            anunturiNavController?.let { resetAnunturiStack(it) }
                                        }
                                        if (index == 2 && selectedItemIndex == 2) {
                                            profileNavController?.let { resetProfileStack(it) }
                                        }
                                        selectedItemIndex = index
                                    },
                                    label = { Text(item.title) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                        selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                        indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                        unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding).fillMaxSize()) {
                        when (selectedItemIndex) {
                            0 -> ExploreazaScreen(
                                onNavControllerReady = { controller ->
                                    anunturiNavController = controller
                                }
                            )

                            1 -> {
                                val context = LocalContext.current
                                val tokenManager = remember { TokenManager(context) }
                                val token = tokenManager.getToken()
                                if (token == null) {
                                    // Redirecționăm direct din Main, fără să mai intrăm în AdaugaScreen
                                    LaunchedEffect(Unit) {
                                        selectedItemIndex = 2
                                        setariNavHostController?.navigate(SetariRoutes.Login.route)
                                    }
                                } else {
                                    AdaugaScreen(
                                        onSuccess = { selectedItemIndex = 0 },
                                        onNavigateToLogin = { /* Deja verificat mai sus */ }
                                    )
                                }
//                            }AdaugaScreen(
//                                onSuccess = {
//                                    selectedItemIndex = 0
//                                    anunturiNavController?.navigate(AnunturiRoutes.LIST) {
//                                        popUpTo(AnunturiRoutes.LIST) { inclusive = true }
//                                    }
//                                },
//                                onNavigateToLogin = {
////                                    selectedItemIndex = 2
////                                    setariNavHostController?.let { controller ->
////                                        controller.navigate(SetariRoutes.Login.route) {
////                                            launchSingleTop = true
////                                        }
////                                    }
//                                }
//                            )
                            }
                            //2 -> ChatScreen()
                            2 -> SetariScreen(
                                onNavigateToGlobalDetail = { id ->
                                    println("Navigat la anuntul: $id")
                                    anunturiNavController?.navigate(AnunturiRoutes.detailsRoute(id))
                                },
                                onProfileNavControllerReady = { controller ->
                                    profileNavController = controller
                                    setariNavHostController = controller
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}


