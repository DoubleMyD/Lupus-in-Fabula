package com.example.lupusinfabulav1

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
import com.example.lupusinfabulav1.model.PlayersForRoleEvent
import com.example.lupusinfabulav1.model.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.NewPlayerViewModel

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
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = LupusInFabulaScreen.valueOf(
        backStackEntry?.destination?.route ?: LupusInFabulaScreen.HOME_PAGE.name
    )

    val context = LocalContext.current
    val playerSize = playersForRoleViewModel.playersSize

    val playersForRoleUiState by playersForRoleViewModel.uiState.collectAsState()
    val playersForRoleUiEvent = playersForRoleViewModel.uiEvent.collectAsState(initial = null)

    // Observe UI events from the ViewModel
    LaunchedEffect(playersForRoleUiEvent.value) {
        when (playersForRoleUiEvent.value) {
            PlayersForRoleEvent.ErrorNotAllPlayersSelected -> {
                Toast.makeText(context, R.string.error_not_all_players_selected, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> Unit // Handle other potential events
        }
    }

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
            startDestination = LupusInFabulaScreen.NEW_PLAYER.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = LupusInFabulaScreen.NEW_PLAYER.name) {
                NewPlayerScreen(
                    onConfirmClick = { name, imageRes ->
                        if (newPlayerViewModel.onAddPlayer(name, imageRes))
                            navController.navigate(LupusInFabulaScreen.PLAYERS_FOR_ROLE.name)
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
                    onCancelClick = { navController.navigate(LupusInFabulaScreen.PLAYERS_FOR_ROLE.name) },
                    playerSize = playerSize,
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
        }
    }
}