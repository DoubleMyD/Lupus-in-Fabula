package com.example.lupusinfabulav1.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lupusinfabulav1.ui.home.HomePageScreen
import com.example.lupusinfabulav1.ui.navigation.setup.NavigationAction
import com.example.lupusinfabulav1.ui.navigation.setup.Navigator
import com.example.lupusinfabulav1.ui.navigation.setup.ObserveAsEvents
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

fun NavGraphBuilder.addTiedScreens(
    navController: NavHostController,
    screenHandler: NavGraphBuilder.(NavHostController) -> Unit
) {
    // This will invoke the screen handler to add the tied screens
    screenHandler(navController)
}

sealed interface Destination {
    @Serializable
    data object HomePage : Destination
}


@Composable
fun MainNavHost2(
    navController: NavHostController = rememberNavController(),
) {
    val navigator = koinInject<Navigator>()

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when(action) {
            is NavigationAction.Navigate -> navController.navigate(
                action.destination
            ) {
                action.navOptions(this)
            }
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination
    ) {
        composable<Destination.HomePage> {
            // Home page screen that can navigate to different parts of the app
            HomePageScreen(
                onNavigateToPlayers = { navController.navigate(PlayerDestination.Players) },
                onNavigateToPlayersLists = { navController.navigate(PlayersListDestination.PlayersLists) },
                onNavigateToGame = { navController.navigate(GameDestination.ChoosePlayersList) }
            )
        }

        addTiedScreens(
            navController = navController,
            screenHandler = { playerNavigation(navController) }
        )

        addTiedScreens(
            navController = navController,
            screenHandler = { playersListNavigation(navController) }
        )

        addTiedScreens(
            navController = navController,
            screenHandler = { gameNavigation(navController) }
        )


    }
}