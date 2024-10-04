package com.example.lupusinfabulav1.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lupusinfabulav1.LupusInFabulaApplication
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.PlayerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Define possible UI events, such as showing a message
sealed class NewPlayerEvent{
    data object ErrorNameNotAvailable: NewPlayerEvent()
}

class NewPlayerViewModel(
    val playerManager: PlayerManager
) : ViewModel() {
    // Event channel for UI interactions like Toasts or Dialogs
    private val _uiEvent = MutableSharedFlow<NewPlayerEvent>()
    val uiEvent: SharedFlow<NewPlayerEvent> = _uiEvent.asSharedFlow()

    /**
     * Add a new player to the game.
     * return false if the name is not available or already taken, true if the player is added successfully
     */
    fun addPlayer(name: String, imageSource: PlayerImageSource) : Boolean {
        if (isNameAvailable(name)) {
            val newPlayer = Player(name = name, imageSource = imageSource)
            playerManager.addPlayer(newPlayer)
            return true
        } else {
            viewModelScope.launch {
                _uiEvent.emit(NewPlayerEvent.ErrorNameNotAvailable)
            }
            return false
        }
    }

    private fun isNameAvailable(name: String) : Boolean{
        return !playerManager.players.value.any { it.name == name } && name.isNotEmpty()
    }

    // Factory for Dependency Injection
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LupusInFabulaApplication)
                val playerManager = application.container.playerManager
                NewPlayerViewModel(playerManager = playerManager)
            }
        }
    }
}