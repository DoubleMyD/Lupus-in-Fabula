package com.example.lupusinfabulav1.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayer
import com.example.lupusinfabulav1.ui.game.PlayersForRoleScreen
import com.example.lupusinfabulav1.ui.game.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.game.VillageScreen7
import com.example.lupusinfabulav1.ui.game.VillageViewModel
import com.example.lupusinfabulav1.ui.home.HomePageScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.player.PlayersEditListDestination
import com.example.lupusinfabulav1.ui.player.PlayersEditListViewModel
import com.example.lupusinfabulav1.ui.player.PlayersScreen
import com.example.lupusinfabulav1.ui.playersList.EditPlayersListDestination
import com.example.lupusinfabulav1.ui.playersList.EditPlayersListScreen
import com.example.lupusinfabulav1.ui.playersList.EditPlayersListViewModel
import com.example.lupusinfabulav1.ui.playersList.PlayersListsScreen
import com.example.lupusinfabulav1.ui.playersList.PlayersListsViewModel

enum class LupusInFabulaScreen(val title: String) {
    HOME_PAGE("Home Page"),  //Pagina di ingresso
    NEW_PLAYER("New Player"), //Pagina per aggiungere un giocatore
    PLAYERS_FOR_ROLE("Players for Role"),   //Pagina per assegnare il numero di giocatori per ruolo (3 lupi, 1 cupido ecc.)
    //PLAYERS_ROLE("Players Role"),       //Pagina per assegnare un ruolo a un giocatore
    VILLAGE("Village"), //Pagina in cui avviene il gioco
    PLAYERS("Players"),  //Pagina per vedere la lista dei giocatori
    //PLAYER_INFO("Player Info"),    //Pagina per vedere le informazioni di un giocatore
    PLAYERS_LISTS("Players_Lists"),    //Pagina per vedere la lista dei giocatori
}

@Composable
fun LupusInFabulaApp(
    navController: NavHostController = rememberNavController(),
    newPlayerViewModel: NewPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playersForRoleViewModel: PlayersForRoleViewModel = viewModel(factory = AppViewModelProvider.Factory),
    villageViewModel: VillageViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playersListsViewModel: PlayersListsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    editPlayersListViewModel: EditPlayersListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playersEditListViewModel: PlayersEditListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current

    // Collect UI events for each ViewModel
    HandleNewPlayerEvents(newPlayerViewModel, context)
    HandlePlayersForRoleEvents(playersForRoleViewModel, context)
    HandleVillageEvents(villageViewModel, context)

    val playersForRoleUiState by playersForRoleViewModel.uiState.collectAsState()
    val villageUiState by villageViewModel.uiState.collectAsState()
    val playersListUiState by playersListsViewModel.uiState.collectAsState()
    val editPlayersListUiState by editPlayersListViewModel.uiState.collectAsState()
    val databasePlayersUiState by playersEditListViewModel.databasePlayersUiState.collectAsState()
    val playersEditListUiState by playersEditListViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = LupusInFabulaScreen.HOME_PAGE.name,
        modifier = Modifier
            .fillMaxSize()
        //.verticalScroll(rememberScrollState())
        //.padding(innerPadding)
    ) {
        composable(route = LupusInFabulaScreen.HOME_PAGE.name) {
            HomePageScreen(
                onNavigateToNewPlayer = { navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name) },
                onNavigateToPlayersForRole = { navController.navigate(LupusInFabulaScreen.PLAYERS_FOR_ROLE.name) },
                onNavigateToVillage = { navController.navigate(LupusInFabulaScreen.VILLAGE.name) },
                onNavigateToPlayersList = { navController.navigate(LupusInFabulaScreen.PLAYERS_LISTS.name) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = LupusInFabulaScreen.NEW_PLAYER.name) {
            NewPlayerScreen(
                viewModel = newPlayerViewModel,
                navigateUp = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
                onConfirmClick = { navController.navigate(PlayersEditListDestination) },
                onCancelClick = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
            )
        }
        composable(route = LupusInFabulaScreen.PLAYERS_FOR_ROLE.name) {
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
        composable(route = LupusInFabulaScreen.VILLAGE.name) {
            VillageScreen7(
                navigateUp = { navController.navigateUp() },
                uiState = villageUiState,
                onCenterIconTap = villageViewModel::nextRole,
                onCenterIconLongPress = { },    //navController.navigate(LupusInFabulaScreen.PLAYERS_LIST.name)
                onPlayerTap = villageViewModel::vote,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = LupusInFabulaScreen.PLAYERS_LISTS.name) {
            PlayersListsScreen(
                navigateUp = { navController.navigateUp() },
                onListClick = { listId ->
                    navController.navigate(EditPlayersListDestination(listId = listId.toInt()))
                },
                uiState = playersListUiState,
                onCreateNewList = { name: String -> playersListsViewModel.addNewList(name) },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = LupusInFabulaScreen.PLAYERS.name) {
            PlayersScreen(
                navigateUp = { navController.navigateUp() },
                onFloatingButtonClick = { navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name) },
                playersDetails = databasePlayersUiState.playersDetails,
            )
        }

        composable<EditPlayersListDestination> {
            val args = it.toRoute<EditPlayersListDestination>()
            //editPlayersListViewModel.loadPlayersDetailsFromPlayersList2(args.listId)

            // Ensure `initScreen` is called only once when navigating to this screen
            LaunchedEffect(key1 = args.listId, key2 = editPlayersListUiState.playersDetails) {
                editPlayersListViewModel.loadPlayersDetailsFromPlayersList(args.listId)
            }

            EditPlayersListScreen(
                navigateUp = { navController.navigate(LupusInFabulaScreen.PLAYERS_LISTS.name) },
                //navigateBack = { navController.navigate(LupusInFabulaScreen.PLAYERS_LISTS.name) },
                onFloatingButtonClick = {
                    navController.navigate(
                        PlayersEditListDestination(
                            listId = editPlayersListUiState.listId,
                            playersIdList = editPlayersListUiState.playersDetails.map { playerDetails ->
                                playerDetails.toPlayer(
                                    context
                                ).id
                            }
                        )
                    )

                },
                uiState = editPlayersListUiState,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<PlayersEditListDestination> { it ->
            val args = it.toRoute<PlayersEditListDestination>()

            // Ensure `initScreen` is called only once when navigating to this screen
            LaunchedEffect(key1 = args.listId, key2 = args.playersIdList) {
                playersEditListViewModel.initScreen(args.listId, args.playersIdList)
            }

            PlayersScreen(
                navigateUp = { navController.navigate(EditPlayersListDestination(args.listId)) },
                floatingButtonImage = {
                    if (playersEditListUiState.selectedPlayers.isEmpty()) {
                        Icons.Default.Add
                    } else Icons.Default.Check
                },
                onFloatingButtonClick = {
                    if (playersEditListUiState.selectedPlayers.isEmpty())
                        navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name)

                    val listId = playersEditListUiState.listId
                    val addedPlayers = playersEditListUiState.selectedPlayers.filter { player -> player.id !in args.playersIdList }
                    val removedPlayers = args.playersIdList.filter { playerId -> playerId !in playersEditListUiState.selectedPlayers.map { it.id } }

                    playersEditListViewModel.updatePlayersIdOfList(
                        listId = listId,
                        addedPlayers = addedPlayers.map { it.id },
                        removedPlayers = removedPlayers
                    )
                    // Navigate back to EditPlayerScreen with the updated listId
                    navController.navigate( EditPlayersListDestination( listId ) )
                },
                playersDetails = databasePlayersUiState.playersDetails,
                cardBackgroundColor = { playerDetails: PlayerDetails ->
                    when {
                        !playersEditListUiState.selectedPlayers.contains(playerDetails) && args.playersIdList.contains(
                            playerDetails.id
                        ) -> Color.DarkGray

                        playersEditListUiState.selectedPlayers.contains(playerDetails) -> Color.LightGray
                        else -> Color.White
                    }
                },
                playerCardAlpha = { playerDetails: PlayerDetails ->
                    when {
                        !playersEditListUiState.selectedPlayers.contains(playerDetails) && args.playersIdList.contains(
                            playerDetails.id
                        ) -> 0.5f

                        else -> 1f
                    }
                },
                onPlayerClick = { playerDetails ->
                    playersEditListViewModel.updateSelectedPlayers(playerDetails)
                },
            )
        }
    }
}