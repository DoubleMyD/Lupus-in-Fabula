package com.example.lupusinfabulav1.ui.navigation

//import com.example.lupusinfabulav1.ui.AppViewModelProvider
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.lupusinfabulav1.ui.game.ChoosePlayersListScreen
import com.example.lupusinfabulav1.ui.game.ChoosePlayersListViewModel
import com.example.lupusinfabulav1.ui.game.PlayersForRoleScreen
import com.example.lupusinfabulav1.ui.game.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.game.VillageScreen7
import com.example.lupusinfabulav1.ui.game.VillageViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


sealed interface GameDestination : Destination {
    @Serializable
    data object Village : GameDestination

    @Serializable
    data object PlayersForRole : GameDestination

    @Serializable
    data object ChoosePlayersList : GameDestination
}

fun NavGraphBuilder.gameNavigation(
    navController: NavHostController,
) {

    composable<GameDestination.ChoosePlayersList> {
        val choosePlayersListViewModel: ChoosePlayersListViewModel =
            koinViewModel<ChoosePlayersListViewModel>()
        val choosePlayersListUiState by choosePlayersListViewModel.uiState.collectAsState()

        ChoosePlayersListScreen(
            navigateUp = { navController.navigateUp() },
            onListClick = { listId ->
                navController.navigate(
                    PlayersListDestination.InfoPlayersList(
                        listId = listId.toInt(),
                        readOnlyMode = true
                    )
                )
            },
            onListLongClick = { listId -> choosePlayersListViewModel.updateSelectedListId(listId) },
            onConfirmClick = {
                val addOk = choosePlayersListViewModel.addPlayersToGame()
                if (addOk)
                navController.navigate(GameDestination.PlayersForRole)
            },
            uiState = choosePlayersListUiState,
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<GameDestination.PlayersForRole> {
        val playersForRoleViewModel: PlayersForRoleViewModel =
            koinViewModel<PlayersForRoleViewModel>()
        val playersForRoleUiState by playersForRoleViewModel.uiState.collectAsState()

        PlayersForRoleScreen(
            navigateUp = { navController.navigateUp() },
            onConfirmClick = {
                if (playersForRoleViewModel.checkIfAllPlayersSelected()) {
                    playersForRoleViewModel.assignRoleToPlayers()
                    navController.navigate(GameDestination.Village)
                }
            },
            onCancelClick = { navController.navigateUp() },
            uiState = playersForRoleUiState,
            onSliderValueChange = { newValue ->
                playersForRoleViewModel.updateSliderValue(//checkAndUpdateSliderValue(
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

    composable<GameDestination.Village> {
        val villageViewModel: VillageViewModel = koinViewModel<VillageViewModel>()
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
}
