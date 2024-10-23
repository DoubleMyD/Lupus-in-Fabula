package com.example.lupusinfabulav1.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.ui.playersList.PlayersListsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChoosePlayersListUiState(
    val playersLists: Map<PlayersList, List<PlayerDetails>> = emptyMap(),
    val selectedListId: Int? = null
)

class ChoosePlayersListViewModel(
    private val playersListsRepository: PlayersListsRepository,
    private val playerManager: PlayerManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChoosePlayersListUiState())
    val uiState: StateFlow<ChoosePlayersListUiState> = _uiState.asStateFlow()

    init {
        loadAllListsPlayersDetails()
    }

    private fun loadAllListsPlayersDetails() {
        viewModelScope.launch {
            // Call the repository function to fetch all lists with player details
            val updatedPlayersLists = playersListsRepository.getAllListsWithPlayersDetails()

            // Update the UI state
            _uiState.update { currentState ->
                currentState.copy(playersLists = updatedPlayersLists)
            }
        }
    }

    fun updateSelectedListId(listId: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedListId = listId)
        }
    }

    fun addPlayersToGame(): Boolean {
        val selectedListId = _uiState.value.selectedListId
        if (selectedListId == null) {
            Log.d("ChoosePlayersListViewModel", "No list selected")
            return false
        }

        val listPlayers = _uiState.value.playersLists.filter { it.key.id == selectedListId }.values.firstOrNull() ?: emptyList()
        if(listPlayers.size < 4 ){
            Log.d("ChoosePlayersListViewModel", "Not enough players selected")
            return false
        }

        viewModelScope.launch {
            val players = playersListsRepository.getPlayersDetailsFromPlayersList(selectedListId)

            // Use the spread operator to pass the list as vararg
            playerManager.addPlayers(*players.toTypedArray())
        }
        return true
    }
}