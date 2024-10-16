package com.example.lupusinfabulav1.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lupusinfabulav1.ui.AppViewModelProvider
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.game.PlayersForRoleScreen
import com.example.lupusinfabulav1.ui.game.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.game.VillageScreen7
import com.example.lupusinfabulav1.ui.game.VillageViewModel

enum class GameScreen {
    VILLAGE,
    PLAYERS_FOR_ROLE
}

@Composable
fun GameNavGraph(
    navController: NavHostController,
    villageViewModel: VillageViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playersForRoleViewModel: PlayersForRoleViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = GameScreen.VILLAGE.name
    ) {
        composable(route = LupusInFabulaScreen.VILLAGE.name) {
            val villageUiState by villageViewModel.uiState.collectAsState()

            VillageScreen7(
                navigateUp = { navController.navigateUp() },
                uiState = villageUiState,
                onCenterIconTap = villageViewModel::nextRole,
                onCenterIconLongPress = { },    //navController.navigate(LupusInFabulaScreen.PLAYERS_LIST.name)
                onPlayerTap = villageViewModel::vote,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = GameScreen.PLAYERS_FOR_ROLE.name) {
            val playersForRoleUiState by playersForRoleViewModel.uiState.collectAsState()

            PlayersForRoleScreen(
                navigateUp = { navController.navigateUp() },
                onConfirmClick = {
                    if (playersForRoleViewModel.checkIfAllPlayersSelected())
                        navController.navigate(LupusInFabulaScreen.HOME_PAGE.name)
                },
                onCancelClick = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
                uiState = playersForRoleUiState,
                onSliderValueChange = { newValue ->
                    playersForRoleViewModel.checkAndUpdateSliderValue(
                        newValue
                    )
                },
                onRandomizeAllClick = { playersForRoleViewModel.onRandomizeAllClick() },
                onRoleSelection = { selectedRole ->
                    playersForRoleViewModel.updateCurrentRole(
                        selectedRole
                    )
                },
                onRandomNumberClick = { playersForRoleViewModel.onRandomNumberClick() }
            )
        }
    }
}