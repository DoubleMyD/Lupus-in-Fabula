package com.example.lupusinfabulav1.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.home.HomePageScreen

enum class MainScreen {
    PLAYERS_NAV,
    PLAYERS_LISTS_NAV,
    GAME_NAV,
    HOME_PAGE
}

@Composable
fun MainNavHost(
    mainNavController: NavHostController = rememberNavController(),
    startDestination: MainScreen = MainScreen.HOME_PAGE // or another start screen
) {
    NavHost(
        navController = mainNavController,
        startDestination = startDestination.name
    ) {
        // Define routes for each part of the app
        composable(MainScreen.PLAYERS_NAV.name) {
            val playerNavController = rememberNavController()  // Nested navController for Player screens
            PlayerNavGraph(
                mainNavController = mainNavController,
                navController = playerNavController
            )
        }

        composable(MainScreen.PLAYERS_LISTS_NAV.name) {
            val playersListNavController = rememberNavController()  // Nested navController for PlayersList screens
            PlayersListNavGraph(
                mainNavController = mainNavController,
                navController = playersListNavController
            )
        }

        composable(MainScreen.GAME_NAV.name) {
            val gameNavController = rememberNavController()  // Nested navController for Game screens
            GameNavGraph(
                mainNavController = mainNavController,
                navController = gameNavController
            )
        }

        composable(MainScreen.HOME_PAGE.name) {
            // Home page screen that can navigate to different parts of the app
            HomePageScreen(
                onNavigateToPlayers = { mainNavController.navigate(MainScreen.PLAYERS_NAV.name) },
                onNavigateToPlayersLists = { mainNavController.navigate(MainScreen.PLAYERS_LISTS_NAV.name) },
                onNavigateToGame = { mainNavController.navigate(MainScreen.GAME_NAV.name) }
            )
        }
    }
}
