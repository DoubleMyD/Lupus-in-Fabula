package com.example.lupusinfabulav1.ui.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
//import com.example.lupusinfabulav1.ui.AppViewModelProvider
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerScreen
import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.player.PlayersScreen
import com.example.lupusinfabulav1.ui.player.PlayersViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface PlayerDestination : Destination {
    @Serializable
    data object Players : PlayerDestination

    @Serializable
    data class NewPlayer(
        val returnToHomeScreen: Boolean,
        val returnToPlayers: Boolean,
        val returnToEditPlayersList: Boolean,
        val playersListId: Int? = null, //value for EditPlayersList
        val playersId: List<Int> = emptyList(), //value for EditPlayersList
    ) : PlayerDestination
}

fun NavGraphBuilder.playerNavigation(
    navController: NavHostController,
) {
    composable<PlayerDestination.Players> {
        val playersViewModel: PlayersViewModel = koinViewModel<PlayersViewModel>()
        val databasePlayers by playersViewModel.databasePlayers.collectAsState()
        PlayersScreen(
            navigateUp = { navController.navigateUp() },
            onFloatingButtonClick = { navController.navigate(PlayerDestination.NewPlayer(
                returnToHomeScreen = false,
                returnToEditPlayersList = false,
                returnToPlayers = true
            )
            )
                                    },
            floatingButtonImage = { Icons.Default.Add },
            playersDetails = databasePlayers.playersDetails,
        )
    }
    composable<PlayerDestination.NewPlayer> {
        val newPlayerViewModel: NewPlayerViewModel = koinViewModel<NewPlayerViewModel>()
        val args = it.toRoute<PlayerDestination.NewPlayer>()

        val coroutineScope = rememberCoroutineScope()

        val destination = when{
            args.returnToHomeScreen -> Destination.HomePage
            args.returnToPlayers -> PlayerDestination.Players
            args.returnToEditPlayersList -> PlayersListDestination.EditPlayersList(
                listId = args.playersListId!!,
                playersIdList = args.playersId
            )

            else -> {GameDestination.Village}
        }

        NewPlayerScreen(
            navigateUp = { navController.navigate(Destination.HomePage) },
            onConfirmClickAction = { context, name, bitmap ->
                newPlayerViewModel.savePlayer(context, name, bitmap)
            },
            onConfirmClickNavigation = { navController.navigate(destination) },
            onCancelClick = { navController.navigate(Destination.HomePage) },
        )
    }
}
