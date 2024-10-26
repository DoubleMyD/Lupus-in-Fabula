package com.example.lupusinfabulav1.data.di

import com.example.lupusinfabulav1.data.OfflinePlayersListsRepository
import com.example.lupusinfabulav1.data.OfflinePlayersRepository
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.data.database.ImageIO
import com.example.lupusinfabulav1.data.database.LupusInFabulaDatabase
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Randomizer
import com.example.lupusinfabulav1.model.RoundResultManager
import com.example.lupusinfabulav1.model.ValidRangeManager
import com.example.lupusinfabulav1.model.VoteManager
import com.example.lupusinfabulav1.ui.game.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.game.VillageViewModel
import com.example.lupusinfabulav1.ui.navigation.setup.DefaultNavigator
import com.example.lupusinfabulav1.ui.navigation.Destination
import com.example.lupusinfabulav1.ui.navigation.setup.Navigator
import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.playersList.EditPlayersListViewModel
import com.example.lupusinfabulav1.ui.player.PlayersViewModel
import com.example.lupusinfabulav1.ui.playersList.InfoPlayersListViewModel
import com.example.lupusinfabulav1.ui.playersList.PlayersListsViewModel
import com.example.lupusinfabulav1.ui.game.ChoosePlayersListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Navigator> {
        DefaultNavigator(startDestination = Destination.HomePage)
    }
// AppContainer dependencies
    single<PlayersRepository> {
        OfflinePlayersRepository(LupusInFabulaDatabase.getDatabase(get()).playerDao())
    }

    single<PlayersListsRepository> {
        OfflinePlayersListsRepository(
            playersRepository = get(),
            playersListDao = LupusInFabulaDatabase.getDatabase(get()).playersListDao()
        )
    }

    single<PlayerManager> { PlayerManager() }
    single<VoteManager> { VoteManager() }
    single<ImageIO> { ImageIO() }
    single<Randomizer> { Randomizer() }
    single<ValidRangeManager> { ValidRangeManager() }
    single<RoundResultManager> { RoundResultManager() }


    // ViewModels
    viewModel { NewPlayerViewModel(playersRepository = get(), imageIO = get(), playerManager = get()) }
    viewModel { PlayersViewModel(playersRepository = get()) }
    viewModel { PlayersListsViewModel(playersListsRepository = get(), playerManager = get()) }
    viewModel { PlayersForRoleViewModel(playerManager = get(), validRangeManager = get(), randomizer = get()) }
    viewModel { VillageViewModel(playerManager = get(), voteManager = get(), roundResultManager = get()) }
    viewModel { InfoPlayersListViewModel(playersListsRepository = get()) }
    viewModel { EditPlayersListViewModel(playersRepository = get(), playersListsRepository = get(), navigator = get()) }
    viewModel { ChoosePlayersListViewModel(playersListsRepository = get(), playerManager = get()) }
}