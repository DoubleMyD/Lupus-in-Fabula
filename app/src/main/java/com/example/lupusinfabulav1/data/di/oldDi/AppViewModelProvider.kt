package com.example.lupusinfabulav1.data.di.oldDi

//
//object AppViewModelProvider {
//
//    val Factory = viewModelFactory {
//        initializer {
//            NewPlayerViewModel(
//                playersRepository = LupusInFabulaApplication().container.playersRepository,
//                imageIO = LupusInFabulaApplication().container.imageIO,
//                playerManager = LupusInFabulaApplication().container.playerManager
//            )
//        }
//
//        initializer {
//            PlayersViewModel(
//                playersRepository = LupusInFabulaApplication().container.playersRepository,
//            )
//        }
//
//        initializer {
//            PlayersListsViewModel(
//                playersListsRepository = LupusInFabulaApplication().container.playersListsRepository,
//                playerManager = LupusInFabulaApplication().container.playerManager,
//            )
//        }
//
//        initializer {
//            PlayersForRoleViewModel(
//                playerManager = LupusInFabulaApplication().container.playerManager
//            )
//        }
//
//        initializer {
//            VillageViewModel(
//                playerManager = LupusInFabulaApplication().container.playerManager,
//                voteManager = LupusInFabulaApplication().container.voteManager
//            )
//        }
//
//        initializer {
//            EditPlayersListViewModel(
//                playersListsRepository = LupusInFabulaApplication().container.playersListsRepository
//            )
//        }
//
//        initializer {
//            PlayersEditListViewModel(
//                playersRepository = LupusInFabulaApplication().container.playersRepository,
//                playersListsRepository = LupusInFabulaApplication().container.playersListsRepository,
//            )
//        }
//    }
//}
//
///**
// * Extension function to queries for [Application] object and returns an instance of
// * [LupusInFabulaApplication].
// */
//fun CreationExtras.LupusInFabulaApplication(): LupusInFabulaApplication =
//    (this[AndroidViewModelFactory.APPLICATION_KEY] as LupusInFabulaApplication)