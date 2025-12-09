package com.example.adoptie.anunt

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun ExploreazaScreen(modifier: Modifier = Modifier) {

    val anunturiNavController = rememberNavController()

    NavHost(
        navController = anunturiNavController,
        startDestination = AnunturiRoutes.LIST,
        modifier = modifier
    ){
        //ruta pentru lista
        composable(AnunturiRoutes.LIST){
            ExploreazaListScreen(
                onNavigateToDetails = { anuntId ->
                    anunturiNavController.navigate(AnunturiRoutes.detailsRoute(anuntId))
                }
            )
        }

        composable(
            AnunturiRoutes.DETAILS,
            arguments = listOf(navArgument("anuntId"){
                type = NavType.LongType
            })
        ) { backStactEntry ->
            val anuntId = backStactEntry.arguments?.getLong("anuntId")
            if (anuntId != null) {
                DetaliiAnuntScreen(anuntId = anuntId)
            } else {
                Text("Eroare: ID-ul nu exista.")
            }
        }
    }


}
