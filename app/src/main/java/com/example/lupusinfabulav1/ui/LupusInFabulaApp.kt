package com.example.lupusinfabulav1.ui

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
import androidx.navigation.compose.rememberNavController
import com.example.lupusinfabulav1.ui.game.PlayersForRoleScreen
import com.example.lupusinfabulav1.ui.game.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.game.VillageScreen7
import com.example.lupusinfabulav1.ui.game.VillageViewModel
import com.example.lupusinfabulav1.ui.home.HomePageScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.player.PlayersListScreen
import com.example.lupusinfabulav1.ui.player.PlayersListViewModel

enum class LupusInFabulaScreen(val title: String) {
    HOME_PAGE("Home Page"),  //Pagina di ingresso
    NEW_PLAYER("New Player"), //Pagina per aggiungere un giocatore
    PLAYERS_FOR_ROLE("Players for Role"),   //Pagina per assegnare il numero di giocatori per ruolo (3 lupi, 1 cupido ecc.)
    PLAYERS_ROLE("Players Role"),       //Pagina per assegnare un ruolo a un giocatore
    VILLAGE("Village"), //Pagina in cui avviene il gioco
    PLAYER_INFO("Player Info"),    //Pagina per vedere le informazioni di un giocatore
    PLAYERS_LIST("Player List"),    //Pagina per vedere la lista dei giocatori
}

@Composable
fun LupusInFabulaApp(
    navController: NavHostController = rememberNavController(),
    newPlayerViewModel: NewPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playersForRoleViewModel: PlayersForRoleViewModel = viewModel(factory = AppViewModelProvider.Factory),
    villageViewModel: VillageViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playersListViewModel: PlayersListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current

    // Collect UI events for each ViewModel
    HandleNewPlayerEvents(newPlayerViewModel, context)
    HandlePlayersForRoleEvents(playersForRoleViewModel, context)
    HandleVillageEvents(villageViewModel, context)

    val playersForRoleUiState by playersForRoleViewModel.uiState.collectAsState()
    val villageUiState by villageViewModel.uiState.collectAsState()
    val databasePlayersListUiState by playersListViewModel.databasePlayersUiState.collectAsState()
    val playersListUiState by playersListViewModel.uiState.collectAsState()

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
                onNavigateToPlayersList = { navController.navigate(LupusInFabulaScreen.PLAYERS_LIST.name) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = LupusInFabulaScreen.NEW_PLAYER.name) {
            NewPlayerScreen(
                viewModel = newPlayerViewModel,
                navigateBack = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
                onConfirmClick = { name, imageSource -> },
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
        composable(
            route = LupusInFabulaScreen.PLAYERS_LIST.name,
//                arguments = listOf(navArgument(PlayersListDestination.playerIdArg) {
//                    type = NavType.IntType
//                })
        ) {
            PlayersListScreen(
                navigateUp = { navController.navigateUp() },
                updatePlayerManager = playersListViewModel::updatePlayerManager,
                playersDetails = databasePlayersListUiState.playersDetails,
                uiState = playersListUiState,
                onPlayerClick = { playersListViewModel.addPlayer(it) },
                onPlayerLongClick = { },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}