package com.example.lupusinfabulav1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lupusinfabulav1.ui.AppViewModelProvider
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.player.PlayersScreen
import com.example.lupusinfabulav1.ui.player.PlayersViewModel

enum class PlayerScreen {
    PLAYERS,
    NEW_PLAYER,
}

@Composable
fun PlayerNavGraph(
    mainNavController: NavHostController,  // Main controller to navigate between different NavHosts
    navController: NavHostController,
    playersViewModel: PlayersViewModel = viewModel(factory = AppViewModelProvider.Factory),
    newPlayerViewModel: NewPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = PlayerScreen.PLAYERS.name
    ) {
        composable(route = PlayerScreen.PLAYERS.name) {
            val databasePlayers by playersViewModel.databasePlayers.collectAsState()
            PlayersScreen(
                navigateUp = { navController.navigateUp() },
                onFloatingButtonClick = { navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name) },
                floatingButtonImage = { Icons.Default.Add },
                playersDetails = databasePlayers.playersDetails,
            )
        }
        composable(route = PlayerScreen.NEW_PLAYER.name) {
            NewPlayerScreen(
                viewModel = newPlayerViewModel,
                navigateUp = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
                onConfirmClick = { navController.navigate(PlayersEditListDestination) },
                onCancelClick = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
            )
        }
    }
}