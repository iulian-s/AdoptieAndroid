package com.example.adoptie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.adoptie.ui.theme.AdoptieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdoptieTheme {
                val items = listOf(
                    NavigationItem(
                        "Acasa",
                        Icons.Filled.Home,
                        Icons.Outlined.Home,
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
                        Icons.Filled.Settings,
                        Icons.Outlined.Settings,
                        hasNews = true
                    )
                )
                var selectedItemIndex by rememberSaveable{
                    mutableStateOf(0)
                }
                Scaffold(
                    bottomBar = {
                        NavigationBar{
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        selectedItemIndex = index
                                        //navController.navigate(item.title)
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

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdoptieTheme {
        Greeting("Android")
    }
}