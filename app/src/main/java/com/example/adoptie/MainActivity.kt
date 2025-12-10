package com.example.adoptie

import android.os.Bundle
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.adoptie.anunt.ExploreazaScreen
import com.example.adoptie.anunt.resetAnunturiStack
import com.example.adoptie.ui.theme.AdoptieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdoptieTheme {
                val items = listOf(
                    NavigationItem(
                        "Exploreaza",
                        Icons.Filled.Home,
                        Icons.Outlined.Home,
                        hasNews = false
                    ),
                    NavigationItem(
                        "Adauga",
                        Icons.Filled.AddCircle,
                        Icons.Outlined.AddCircle,
                        hasNews = false
                    ),
                    NavigationItem(
                        "Chat",
                        Icons.Filled.Email,
                        Icons.Outlined.Email,
                        hasNews = false,
                        badgeCount = 12
                    ),

                    NavigationItem(
                        "Setari",
                        Icons.Filled.Person,
                        Icons.Outlined.Person,
                        hasNews = true
                    )
                )
                var selectedItemIndex by rememberSaveable{
                    mutableStateOf(0)
                }

                var anunturiNavController: NavHostController? by remember {
                    mutableStateOf(null)
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar{
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        if(index == 0 && selectedItemIndex == 0){
                                            anunturiNavController?.let{
                                                resetAnunturiStack(it)
                                            }
                                        }
                                        selectedItemIndex = index
                                    },
                                    label = {
                                         Text(item.title)
                                    },
                                    icon = {
                                        BadgedBox(badge = {
                                            if(item.badgeCount != null){
                                                Badge{
                                                    Text(item.badgeCount.toString())
                                                }
                                            }
                                            else if(item.hasNews){
                                                Badge()
                                            }

                                        }) {
                                            Icon(imageVector = if(index == selectedItemIndex){
                                                item.selectedIcon
                                            } else {
                                                item.selectedIcon
                                                   },
                                                item.title)
                                        }

                                    }
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
                            1 -> AdaugaScreen()
                            2 -> ChatScreen()
                            3 -> SetariScreen()
                        }
                    }

                }
            }
        }
    }
}

