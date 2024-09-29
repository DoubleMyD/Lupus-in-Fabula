package com.example.lupusinfabulav1

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.lupusinfabulav1.model.NewPlayerEvent
import com.example.lupusinfabulav1.model.NewPlayerViewModel
import com.example.lupusinfabulav1.model.PlayersForRoleEvent
import com.example.lupusinfabulav1.model.PlayersForRoleViewModel
import com.example.lupusinfabulav1.model.VillageEvent
import com.example.lupusinfabulav1.model.VillageViewModel
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize()
            ){
                LupusInFabulaApp()
            }
        }
        /*
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */
    }
}

@Composable
fun LupusInFabulaApp(
    newPlayerViewModel: NewPlayerViewModel = viewModel(),
    playersForRoleViewModel: PlayersForRoleViewModel = viewModel(),
    villageViewModel: VillageViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = LupusInFabulaScreen.valueOf(
        backStackEntry?.destination?.route ?: LupusInFabulaScreen.HOME_PAGE.name
    )

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

@Composable
private fun HandleNewPlayerEvents(viewModel: NewPlayerViewModel, context: Context) {
    val newPlayerUiEvent = viewModel.uiEvent.collectAsState(initial = null)

    LaunchedEffect(newPlayerUiEvent.value) {
        when (newPlayerUiEvent.value) {
            NewPlayerEvent.ErrorNameNotAvailable -> {
                Toast.makeText(context, R.string.error_name_not_available, Toast.LENGTH_SHORT).show()
            }
            else -> Unit // Handle other events if necessary
        }
    }
}

@Composable
private fun HandlePlayersForRoleEvents(viewModel: PlayersForRoleViewModel, context: Context) {
    val playersForRoleUiEvent = viewModel.uiEvent.collectAsState(initial = null)

    LaunchedEffect(playersForRoleUiEvent.value) {
        when (playersForRoleUiEvent.value) {
            PlayersForRoleEvent.ErrorNotAllPlayersSelected -> {
                Toast.makeText(context, R.string.error_not_all_players_selected, Toast.LENGTH_SHORT).show()
            }
            PlayersForRoleEvent.ErrorTooManyPlayersSelected -> {
                Toast.makeText(context, R.string.error_too_many_players_selected, Toast.LENGTH_SHORT).show()
            }
            else -> Unit // Handle other potential events
        }
    }
}

@Composable
private fun HandleVillageEvents(viewModel: VillageViewModel, context: Context) {
    val villageUiEvent = viewModel.uiEvent.collectAsState(initial = null)

    LaunchedEffect(villageUiEvent.value) {
        when (villageUiEvent.value) {
            is VillageEvent.ErrorNotAllPlayersHaveVoted -> {
                Toast.makeText(context, R.string.error_not_all_players_have_voted, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.AllPlayersHaveVoted -> {
                Toast.makeText(context, R.string.all_players_have_voted, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.Tie -> {
                Toast.makeText(context, R.string.tie, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.TieRestartVoting -> {
                Toast.makeText(context, R.string.tie_restart_voting, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.GameNotStarted -> {
                Toast.makeText(context, R.string.game_not_started, Toast.LENGTH_SHORT).show()
            }
            else -> Unit //Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
    }
}