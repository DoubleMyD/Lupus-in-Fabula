package com.example.lupusinfabulav1.ui.navigation.oldNavigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navigation
//import com.example.lupusinfabulav1.LupusInFabulaApplication
//import com.example.lupusinfabulav1.ui.home.HomePageScreen
//import kotlinx.serialization.Serializable
//import org.koin.compose.koinInject
//
////
////enum class MainScreen {
////    PLAYERS_NAV,
////    PLAYERS_LISTS_NAV,
////    GAME_NAV,
////    HOME_PAGE
////}
//
//private sealed interface MainGraphDestination : Destination {
//    @Serializable
//    data object HomePage : MainGraphDestination
//
//    @Serializable
//    data object PlayersListGraph : MainGraphDestination
//
//    @Serializable
//    data object GameGraph : MainGraphDestination
//
//    @Serializable
//    data object PlayerGraph : MainGraphDestination
//}
//
//@Composable
//fun MainNavHost(
//    mainNavController: NavHostController = rememberNavController(),
//) {
//    val navigator = koinInject<Navigator>()
//
//    ObserveAsEvents(flow = navigator.navigationActions) { action ->
//        when (action) {
//            is NavigationAction.Navigate -> {
//                mainNavController.navigate(action.destination)
//            }
//
//            is NavigationAction.NavigateUp -> mainNavController.navigateUp()
//        }
//    }
//
//    NavHost(
//        navController = mainNavController,
//        startDestination = navigator.startDestination
//    ) {
//        navigation<MainGraphDestination>(
//            startDestination = navigator.startDestination
//        ) {
//
//            navigation<PlayerDestination>(
//                startDestination = navigator.startDestination,
//            ) {
//                // Define routes for each part of the app
//                composable<PlayerDestination.Players> {
//                    val playerNavController =
//                        rememberNavController()  // Nested navController for Player screens
//                    PlayerNavGraph(
//                        mainNavController = mainNavController,
//                        navController = playerNavController
//                    )
//                }
//            }
//            navigation<PlayersListDestination>(
//                startDestination = navigator.startDestination
//            ) {
//                composable<PlayersListDestination.PlayersLists> {
//                    val playersListNavController =
//                        rememberNavController()  // Nested navController for PlayersList screens
//                    PlayersListNavGraph(
//                        mainNavController = mainNavController,
//                        navController = playersListNavController,
//                    )
//                }
//            }
//
//            navigation<GameDestination>(
//                startDestination = navigator.startDestination
//            ) {
//                composable<GameDestination.Village> {
//                    val gameNavController =
//                        rememberNavController()  // Nested navController for Game screens
//                    GameNavGraph(
//                        mainNavController = mainNavController,
//                        navController = gameNavController
//                    )
//                }
//            }
//
//            composable<MainGraphDestination.HomePage> {
//                // Home page screen that can navigate to different parts of the app
//                HomePageScreen(
//                    onNavigateToPlayers = { mainNavController.navigate(MainGraphDestination.PlayerGraph) },
//                    onNavigateToPlayersLists = { mainNavController.navigate(MainGraphDestination.PlayersListGraph) },
//                    onNavigateToGame = { mainNavController.navigate(MainGraphDestination.GameGraph) }
//                )
//            }
//        }
//    }
//
////        // Observe for navigation event and trigger the appropriate navigation action
////        val navigateToPlayerScreen by sharedNavigationViewModel.navigateToPlayerScreen.observeAsState()
////        LaunchedEffect(navigateToPlayerScreen) {
////            navigateToPlayerScreen?.getContentIfNotHandled()?.let {
////                mainNavController.navigate(MainScreen.PLAYERS_NAV.name) {
////                    popUpTo(MainScreen.HOME_PAGE.name) { inclusive = true }
////                }
////            }
////        }
//}

