package com.example.lupusinfabulav1.ui.player

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lupusinfabulav1.LupusInFabulaApplication
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.data.database.ImageIO
import com.example.lupusinfabulav1.data.database.entity.Player
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Role
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Define possible UI events, such as showing a message
sealed class NewPlayerEvent{
    data object ErrorNameNotAvailable: NewPlayerEvent()
}

class NewPlayerViewModel(
    savedStateHandle: SavedStateHandle,
    val playersRepository: PlayersRepository,
    val imageIO: ImageIO,
    val playerManager: PlayerManager
) : ViewModel() {
    // Event channel for UI interactions like Toasts or Dialogs
    private val _uiEvent = MutableSharedFlow<NewPlayerEvent>()
    val uiEvent: SharedFlow<NewPlayerEvent> = _uiEvent.asSharedFlow()

    /**
     * Add a new player to the game.
     * return false if the name is not available or already taken, true if the player is added successfully
     */
    suspend fun savePlayer(context: Context, playerName: String, bitmap: Bitmap) {
        val imageSourceLocation = imageIO.saveImageToStorage(context, bitmap, playerName)

        val newPlayer = Player(0, playerName, Role.CITTADINO, true, imageSourceLocation)
        playersRepository.insertPlayer(newPlayer)
    }

    fun isNameAvailable(name: String) : Boolean{
        /*TODO(refactor to search in the database)*/
        val isNameAvailable = !playerManager.players.value.any { it.name == name } && name.isNotEmpty()
        if (isNameAvailable) {
            return true
        } else {
            viewModelScope.launch {
                _uiEvent.emit(NewPlayerEvent.ErrorNameNotAvailable)
            }
            return false
        }
    }

//    // Factory for Dependency Injection
//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as LupusInFabulaApplication)
//                val playerManager = application.container.playerManager
//                val playersRepository = application.container.playersRepository
//                val imageIO = application.container.imageIO
//                NewPlayerViewModel(playersRepository = playersRepository, playerManager = playerManager, imageIO = imageIO)
//            }
//        }
//    }
}