package com.example.lupusinfabulav1.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lupusinfabulav1.LupusInFabulaApplication
import com.example.lupusinfabulav1.ui.game.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.game.VillageViewModel
import com.example.lupusinfabulav1.ui.player.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.player.PlayersListViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            NewPlayerViewModel(
                this.createSavedStateHandle(),
                LupusInFabulaApplication().container.playersRepository,
                LupusInFabulaApplication().container.imageIO,
                LupusInFabulaApplication().container.playerManager
            )
        }

        initializer {
            PlayersListViewModel(
                this.createSavedStateHandle(),
                LupusInFabulaApplication().container.playersRepository
            )
        }

        initializer {
            PlayersForRoleViewModel(
                this.createSavedStateHandle(),
                LupusInFabulaApplication().container.playerManager
            )
        }

        initializer {
            VillageViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                playersRepository = LupusInFabulaApplication().container.playersRepository,
                playerManager = LupusInFabulaApplication().container.playerManager,
                voteManager = LupusInFabulaApplication().container.voteManager
            )
        }
    }
}

    /**
     * Extension function to queries for [Application] object and returns an instance of
     * [InventoryApplication].
     */
    fun CreationExtras.LupusInFabulaApplication(): LupusInFabulaApplication =
        (this[AndroidViewModelFactory.APPLICATION_KEY] as LupusInFabulaApplication)