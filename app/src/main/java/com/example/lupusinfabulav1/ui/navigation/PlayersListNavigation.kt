package com.example.lupusinfabulav1.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayer
//import com.example.lupusinfabulav1.ui.AppViewModelProvider
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.playersList.EditPlayersListViewModel
import com.example.lupusinfabulav1.ui.player.PlayersScreen
import com.example.lupusinfabulav1.ui.playersList.InfoPlayersListViewModel
import com.example.lupusinfabulav1.ui.playersList.PlayersListInfoScreen
import com.example.lupusinfabulav1.ui.playersList.PlayersListsScreen
import com.example.lupusinfabulav1.ui.playersList.PlayersListsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface PlayersListDestination: Destination {
    @Serializable
    data object PlayersLists: PlayersListDestination

    @Serializable
    data class InfoPlayersList(
        val listId: Int,
        val readOnlyMode: Boolean = false
    ) : PlayersListDestination

    @Serializable
    data class EditPlayersList (
        val listId: Int,
        val playersIdList: List<Int>
    ) : PlayersListDestination
}

fun NavGraphBuilder.playersListNavigation(
    navController: NavHostController,
) {
    composable<PlayersListDestination.PlayersLists> {
        val playersListsViewModel: PlayersListsViewModel = koinViewModel<PlayersListsViewModel>()
        val playersListsUiState by playersListsViewModel.uiState.collectAsState()

        PlayersListsScreen(
            navigateUp = { navController.navigate(Destination.HomePage) },
            onListClick = { listId ->
                navController.navigate(PlayersListDestination.InfoPlayersList(listId = listId.toInt()))
            },
            onListLongClick = { listId ->
                playersListsViewModel.updateSelectedListId(listId)
            },
            uiState = playersListsUiState,
            onCreateNewList = { name: String -> playersListsViewModel.addNewList(name) },
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<PlayersListDestination.InfoPlayersList> {
        val context = LocalContext.current
        val infoPlayersListViewModel: InfoPlayersListViewModel = koinViewModel<InfoPlayersListViewModel>()
        val editPlayersListUiState by infoPlayersListViewModel.uiState.collectAsState()

        val args = it.toRoute<PlayersListDestination.InfoPlayersList>()

        // Ensure `initScreen` is called only once when navigating to this screen
        LaunchedEffect(key1 = args.listId, key2 = editPlayersListUiState.playersDetails) {
            infoPlayersListViewModel.loadPlayersDetailsFromPlayersList(args.listId)
        }

        PlayersListInfoScreen(
            readOnlyMode = args.readOnlyMode,
            navigateUp = { navController.navigateUp() },
//            navigateUp = { navController.navigate(PlayersListDestination.PlayersLists) },
            //navigateBack = { navController.navigate(LupusInFabulaScreen.PLAYERS_LISTS.name) },
            onFloatingButtonClick = {
                navController.navigate(
                    PlayersListDestination.EditPlayersList(
                        listId = editPlayersListUiState.listId,
                        playersIdList = editPlayersListUiState.playersDetails.map { playerDetails ->
                            playerDetails.toPlayer(context).id
                        }
                    )
                )

            },
            uiState = editPlayersListUiState,
            modifier = Modifier.fillMaxSize()
        )
    }

    composable<PlayersListDestination.EditPlayersList> { it ->
        val editPlayersListViewModel: EditPlayersListViewModel = koinViewModel<EditPlayersListViewModel>()

        val databasePlayersUiState by editPlayersListViewModel.databasePlayers.collectAsState()
        val editPlayersListUiState by editPlayersListViewModel.uiState.collectAsState()
        val args = it.toRoute<PlayersListDestination.EditPlayersList>()

        // Ensure `initScreen` is called only once when navigating to this screen
        LaunchedEffect(key1 = args.listId, key2 = args.playersIdList) {
            editPlayersListViewModel.initScreen(args.listId, args.playersIdList)
        }

        PlayersScreen(
            navigateUp = { navController.navigate(PlayersListDestination.InfoPlayersList(args.listId)) },
            floatingButtonImage = {
                if (editPlayersListUiState.selectedPlayersId.isEmpty()) {
                    Icons.Default.Add
                } else Icons.Default.Check
            },
            onFloatingButtonClick = {
                if (editPlayersListUiState.selectedPlayersId.isEmpty()) {
                    editPlayersListViewModel.addNewPlayer()
                    Log.d("PlayersScreen", "Empty selected Players ")
                }else {

                    val listId = editPlayersListUiState.listId
                    val addedPlayers =
                        editPlayersListUiState.selectedPlayersId.filter { playerId -> playerId !in args.playersIdList }
                    val removedPlayers =
                        args.playersIdList.filter { playerId -> playerId !in editPlayersListUiState.selectedPlayersId }

                    editPlayersListViewModel.updatePlayersIdOfList(
                        listId = listId,
                        addedPlayers = addedPlayers,
                        removedPlayers = removedPlayers
                    )
                    // Navigate back to EditPlayerScreen with the updated listId
                    navController.navigate(PlayersListDestination.InfoPlayersList(listId))
                }
            },
            playersDetails = databasePlayersUiState.playersDetails,
            cardBackgroundColor = { playerDetails: PlayerDetails ->
                when {
                    !editPlayersListUiState.selectedPlayersId.contains(playerDetails.id) && args.playersIdList.contains(
                        playerDetails.id
                    ) -> Color.DarkGray

                    editPlayersListUiState.selectedPlayersId.contains(playerDetails.id) -> Color.LightGray
                    else -> Color.White
                }
            },
            playerCardAlpha = { playerDetails: PlayerDetails ->
                when {
                    !editPlayersListUiState.selectedPlayersId.contains(playerDetails.id) && args.playersIdList.contains(
                        playerDetails.id
                    ) -> 0.5f

                    else -> 1f
                }
            },
            onPlayerClick = { playerDetails ->
                editPlayersListViewModel.updateSelectedPlayers(playerDetails)
            },
        )
    }
}