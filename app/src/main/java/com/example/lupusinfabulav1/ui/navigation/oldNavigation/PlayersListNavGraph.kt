package com.example.lupusinfabulav1.ui.navigation.oldNavigation
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.toRoute
//import com.example.lupusinfabulav1.model.PlayerDetails
//import com.example.lupusinfabulav1.model.toPlayer
//import com.example.lupusinfabulav1.ui.AppViewModelProvider
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
//import com.example.lupusinfabulav1.ui.player.PlayersEditListViewModel
//import com.example.lupusinfabulav1.ui.player.PlayersScreen
//import com.example.lupusinfabulav1.ui.playersList.EditPlayersListViewModel
//import com.example.lupusinfabulav1.ui.playersList.PlayersListInfoScreen
//import com.example.lupusinfabulav1.ui.playersList.PlayersListsScreen
//import com.example.lupusinfabulav1.ui.playersList.PlayersListsViewModel
//import kotlinx.serialization.Serializable
//
//enum class PlayersListScreen {
//    PLAYERS_LISTS
//}
//
//@Serializable
//data class PlayersListInfoDestination(
//    val listId: Int,
//) : Destination
//
//@Serializable
//data class PlayersEditListDestination (
//    val listId: Int,
//    val playersIdList: List<Int>
//)
//
//
//@Composable
//fun PlayersListNavGraph(
//    mainNavController: NavHostController,  // Main controller to navigate between different NavHosts
//    navController: NavHostController,
//    //playerNavController: NavController,
//    playersListsViewModel: PlayersListsViewModel = viewModel(factory = AppViewModelProvider.Factory),
//    editPlayersListViewModel: EditPlayersListViewModel = viewModel(factory = AppViewModelProvider.Factory),
//    playersEditListViewModel: PlayersEditListViewModel = viewModel(factory = AppViewModelProvider.Factory),
//    ) {
//    val context = LocalContext.current
//    NavHost(
//        navController = navController,
//        startDestination = PlayersListScreen.PLAYERS_LISTS.name
//    ) {
//        composable(route = PlayersListScreen.PLAYERS_LISTS.name) {
//            val playersListsUiState by playersListsViewModel.uiState.collectAsState()
//
//            PlayersListsScreen(
//                navigateUp = { mainNavController.navigate(Destination.HomePage) },
//                onListClick = { listId ->
//                    navController.navigate(PlayersListInfoDestination(listId = listId.toInt()))
//                },
//                uiState = playersListsUiState,
//                onCreateNewList = { name: String -> playersListsViewModel.addNewList(name) },
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//
//        composable<PlayersListInfoDestination> {
//            val editPlayersListUiState by editPlayersListViewModel.uiState.collectAsState()
//
//            val args = it.toRoute<PlayersListInfoDestination>()
//
//            // Ensure `initScreen` is called only once when navigating to this screen
//            LaunchedEffect(key1 = args.listId, key2 = editPlayersListUiState.playersDetails) {
//                editPlayersListViewModel.loadPlayersDetailsFromPlayersList(args.listId)
//            }
//
//            PlayersListInfoScreen(
//                navigateUp = { navController.navigate(PlayersListScreen.PLAYERS_LISTS.name) },
//                //navigateBack = { navController.navigate(LupusInFabulaScreen.PLAYERS_LISTS.name) },
//                onFloatingButtonClick = {
//                    navController.navigate(
//                        PlayersEditListDestination(
//                            listId = editPlayersListUiState.listId,
//                            playersIdList = editPlayersListUiState.playersDetails.map { playerDetails ->
//                                playerDetails.toPlayer(context).id
//                            }
//                        )
//                    )
//
//                },
//                uiState = editPlayersListUiState,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//
//        composable<PlayersEditListDestination> { it ->
//            val databasePlayersUiState by playersEditListViewModel.databasePlayers.collectAsState()
//            val playersEditListUiState by playersEditListViewModel.uiState.collectAsState()
//            val args = it.toRoute<PlayersEditListDestination>()
//
//            // Ensure `initScreen` is called only once when navigating to this screen
//            LaunchedEffect(key1 = args.listId, key2 = args.playersIdList) {
//                playersEditListViewModel.initScreen(args.listId, args.playersIdList)
//            }
//
//            PlayersScreen(
//                navigateUp = { navController.navigate(PlayersListInfoDestination(args.listId)) },
//                floatingButtonImage = {
//                    if (playersEditListUiState.selectedPlayers.isEmpty()) {
//                        Icons.Default.Add
//                    } else Icons.Default.Check
//                },
//                onFloatingButtonClick = {
//                    if (playersEditListUiState.selectedPlayers.isEmpty())
//                        navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name)
//
//                    val listId = playersEditListUiState.listId
//                    val addedPlayers = playersEditListUiState.selectedPlayers.filter { player -> player.id !in args.playersIdList }
//                    val removedPlayers = args.playersIdList.filter { playerId -> playerId !in playersEditListUiState.selectedPlayers.map { playerDetails -> playerDetails.id } }
//
//                    playersEditListViewModel.updatePlayersIdOfList(
//                        listId = listId,
//                        addedPlayers = addedPlayers.map { it.id },
//                        removedPlayers = removedPlayers
//                    )
//                    // Navigate back to EditPlayerScreen with the updated listId
//                    //navController.navigate( PlayersListInfoDestination( listId ) )
//                    editPlayersListViewModel.addNewPlayer()
//                },
//                playersDetails = databasePlayersUiState.playersDetails,
//                cardBackgroundColor = { playerDetails: PlayerDetails ->
//                    when {
//                        !playersEditListUiState.selectedPlayers.contains(playerDetails) && args.playersIdList.contains(
//                            playerDetails.id
//                        ) -> Color.DarkGray
//
//                        playersEditListUiState.selectedPlayers.contains(playerDetails) -> Color.LightGray
//                        else -> Color.White
//                    }
//                },
//                playerCardAlpha = { playerDetails: PlayerDetails ->
//                    when {
//                        !playersEditListUiState.selectedPlayers.contains(playerDetails) && args.playersIdList.contains(
//                            playerDetails.id
//                        ) -> 0.5f
//
//                        else -> 1f
//                    }
//                },
//                onPlayerClick = { playerDetails ->
//                    playersEditListViewModel.updateSelectedPlayers(playerDetails)
//                },
//            )
//        }
//    }
//}