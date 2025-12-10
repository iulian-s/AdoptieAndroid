package com.example.adoptie.anunt

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.adoptie.utilizator.ProfilUtilizatorScreen


fun resetAnunturiStack(navController: NavHostController){
    navController.popBackStack(
        route = AnunturiRoutes.LIST,
        inclusive = false
    )
}
@Composable
fun ExploreazaScreen(
    modifier: Modifier = Modifier,
    onNavControllerReady: (NavHostController) -> Unit
    ) {

    val anunturiNavController = rememberNavController()
    LaunchedEffect(Unit) {
        onNavControllerReady(anunturiNavController)
    }

    val onNavigateToDetails: (Long) -> Unit = { anuntId ->
        anunturiNavController.navigate(AnunturiRoutes.detailsRoute(anuntId))
    }



    NavHost(
        navController = anunturiNavController,
        startDestination = AnunturiRoutes.LIST,
        modifier = modifier
    ){
        //ruta pentru lista
        composable(AnunturiRoutes.LIST){
            ExploreazaListScreen(
                onNavigateToDetails = onNavigateToDetails
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
                DetaliiAnuntScreen(
                    anuntId = anuntId,
                    onNavigateToProfile = { userId ->
                        anunturiNavController.navigate(AnunturiRoutes.profileRoute(userId))
                    }
                )

            } else {
                Text("Eroare: ID-ul nu exista.")
            }
        }


        composable(
            AnunturiRoutes.USER_PROFILE,
            arguments = listOf(navArgument("userId"){
                type = NavType.LongType
            })
        )
            { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId")
                if(userId != null){
                   ProfilUtilizatorScreen(
                       userId = userId,
                       onNavigateToDetails = onNavigateToDetails
                   )
                } else {
                    Text("Eroare: Id-ul utilizatorului nu exista.")
                }
            }

    }


}
