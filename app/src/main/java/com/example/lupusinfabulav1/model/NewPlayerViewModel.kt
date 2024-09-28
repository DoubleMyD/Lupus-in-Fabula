package com.example.lupusinfabulav1.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Define possible UI events, such as showing a message
sealed class NewPlayerEvent{
    data object ErrorNameNotAvailable: NewPlayerEvent()
}

class NewPlayerViewModel : ViewModel() {
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
            PlayerManager.addPlayer(newPlayer)
            return true
        } else {
            viewModelScope.launch {
                _uiEvent.emit(NewPlayerEvent.ErrorNameNotAvailable)
            }
            return false
        }
    }

    private fun isNameAvailable(name: String) : Boolean{
        return !PlayerManager.players.value.any { it.name == name } && name.isNotEmpty()
    }
}