package com.example.lupusinfabulav1.ui.navigation.oldNavigation
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.lupusinfabulav1.ui.AppViewModelProvider
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
//import com.example.lupusinfabulav1.ui.player.NewPlayerScreen
//import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
//import com.example.lupusinfabulav1.ui.player.PlayersScreen
//import com.example.lupusinfabulav1.ui.player.PlayersViewModel
//import kotlinx.serialization.Serializable
////
////enum class PlayerScreen {
////    PLAYERS,
////    NEW_PLAYER,
////}
////
////@Serializable
////data class NewPlayerDestination(
////    val navigateUp: Unit
////)
//
//
////sealed interface PlayerDestination: Destination {
////    @Serializable
////    data object Players: PlayerDestination
////
////    @Serializable
////    data object NewPlayer : PlayerDestination
////}
//
//@Composable
//fun PlayerNavGraph(
//    startDestination: PlayerDestination = PlayerDestination.Players,
//    mainNavController: NavHostController,  // Main controller to navigate between different NavHosts
//    navController: NavHostController,
//    playersViewModel: PlayersViewModel = viewModel(factory = AppViewModelProvider.Factory),
//    newPlayerViewModel: NewPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
//) {
//    val context = LocalContext.current
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        composable<PlayerDestination.Players> {
//            val databasePlayers by playersViewModel.databasePlayers.collectAsState()
//            PlayersScreen(
//                navigateUp = { navController.navigateUp() },
//                onFloatingButtonClick = { navController.navigate(LupusInFabulaScreen.NEW_PLAYER.name) },
//                floatingButtonImage = { Icons.Default.Add },
//                playersDetails = databasePlayers.playersDetails,
//            )
//        }
//        composable<PlayerDestination.NewPlayer>{
//            NewPlayerScreen(
//                viewModel = newPlayerViewModel,
//                navigateUp = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
//                onConfirmClick = { navController.navigate(PlayersEditListDestination) },
//                onCancelClick = { navController.navigate(LupusInFabulaScreen.HOME_PAGE.name) },
//            )
//        }
//    }
//}