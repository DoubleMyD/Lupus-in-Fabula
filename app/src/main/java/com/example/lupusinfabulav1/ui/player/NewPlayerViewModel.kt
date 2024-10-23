package com.example.lupusinfabulav1.ui.player

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.data.database.ImageIO
import com.example.lupusinfabulav1.data.database.entity.Player
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Role
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okio.IOException

// Define possible UI events, such as showing a message
sealed class NewPlayerEvent{
    data object ErrorNameNotAvailable: NewPlayerEvent()
}

class NewPlayerViewModel(
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
    suspend fun savePlayer(context: Context, playerName: String, bitmap: Bitmap) : Boolean {
        val trimmedPlayerName = playerName.trim()
        if (!isNameAvailable(trimmedPlayerName)) {
            Log.d("SavePlayer", "Name not available")
            _uiEvent.emit(NewPlayerEvent.ErrorNameNotAvailable)
            return false
        }
        try {
            val imageSourceLocation = imageIO.saveImageToStorage(context, bitmap, trimmedPlayerName)

            val newPlayer = Player(0, trimmedPlayerName, Role.CITTADINO, true, imageSourceLocation)
            playersRepository.insertPlayer(newPlayer)
            return true
        } catch(e: IOException){
            Log.e("SavePlayer", "Failed to save player: ${e.message}")
            return false
        }
    }

    // Function to check if a player with the given name exists in the database
    private suspend fun isNameAvailable(name: String): Boolean {
        return playersRepository.getPlayerByName(name) // Returns Flow<Player?>
            .firstOrNull() == null
    }
}