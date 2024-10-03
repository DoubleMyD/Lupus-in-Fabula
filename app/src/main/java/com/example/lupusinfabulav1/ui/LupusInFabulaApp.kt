package com.example.lupusinfabulav1.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lupusinfabulav1.ui.viewModels.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.viewModels.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.viewModels.VillageViewModel
import com.example.lupusinfabulav1.ui.Screens.HomePageScreen
import com.example.lupusinfabulav1.ui.Screens.NewPlayerScreen
import com.example.lupusinfabulav1.ui.Screens.PlayersForRoleScreen
import com.example.lupusinfabulav1.ui.Screens.VillageScreen6
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar

enum class LupusInFabulaScreen(val title: String) {
    HOME_PAGE("Home Page"),  //Pagina di ingresso
    NEW_PLAYER("New Player"), //Pagina per aggiungere un giocatore
    PLAYERS_FOR_ROLE("Players for Role"),   //Pagina per assegnare il numero di giocatori per ruolo (3 lupi, 1 cupido ecc.)
    PLAYERS_ROLE("Players Role"),       //Pagina per assegnare un ruolo a un giocatore
    VILLAGE("Village"), //Pagina in cui avviene il gioco
    PLAYER_INFO("Player Info"),    //Pagina per vedere le informazioni di un giocatore
    PLAYER_LIST("Player List"),    //Pagina per vedere la lista dei giocatori
}

@Composable
fun LupusInFabulaApp(
    newPlayerViewModel: NewPlayerViewModel = viewModel(),
    playersForRoleViewModel: PlayersForRoleViewModel = viewModel(),
    //villageViewModel: VillageViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = LupusInFabulaScreen.valueOf(
        backStackEntry?.destination?.route ?: LupusInFabulaScreen.HOME_PAGE.name
    )

    val villageViewModel : VillageViewModel = viewModel(factory = VillageViewModel.Factory)

    // Collect UI events for each ViewModel
    HandleNewPlayerEvents(newPlayerViewModel, context)
    HandlePlayersForRoleEvents(playersForRoleViewModel, context)
    HandleVillageEvents(villageViewModel, context)

    val playersForRoleUiState by playersForRoleViewModel.uiState.collectAsState()
    val villageUiState by villageViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = LupusInFabulaScreen.HOME_PAGE.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = LupusInFabulaScreen.HOME_PAGE.name) {
                HomePageScreen(
                    onNavigateToNewPlayer = { navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name) },
                    onNavigateToPlayersForRole = { navController.navigate(LupusInFabulaScreen.PLAYERS_FOR_ROLE.name) },
                    onNavigateToVillage = { navController.navigate(LupusInFabulaScreen.VILLAGE.name) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = LupusInFabulaScreen.NEW_PLAYER.name) {
                NewPlayerScreen(
                    onConfirmClick = { name, imageSource ->
                        val isValidPlayer = newPlayerViewModel.addPlayer(name, imageSource)
                        if (isValidPlayer) {
                            navController.navigate(LupusInFabulaScreen.PLAYERS_FOR_ROLE.name)
                        }
                    },
                    onCancelClick = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
                )
            }
            composable(route = LupusInFabulaScreen.PLAYERS_FOR_ROLE.name) {
                PlayersForRoleScreen(
                    onConfirmClick = {
                        if (playersForRoleViewModel.checkIfAllPlayersSelected())
                            navController.navigate(LupusInFabulaScreen.HOME_PAGE.name)
                    },
                    onCancelClick = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
                    uiState = playersForRoleUiState,
                    onSliderValueChange = { newValue ->
                        playersForRoleViewModel.updateSliderValue(
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
                VillageScreen6(
                    uiState = villageUiState,
                    onCenterIconClick = villageViewModel::nextRole,
                    onPlayerTap = villageViewModel::vote,
                    modifier = Modifier.fillMaxSize()
                )
            }

        }
    }
}