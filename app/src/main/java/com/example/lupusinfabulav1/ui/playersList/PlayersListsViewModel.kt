package com.example.lupusinfabulav1.ui.playersList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class PlayersListsUiState(
    val playersLists: Map<PlayersList, List<PlayerDetails>> = emptyMap()
)

class PlayersListsViewModel(
    savedStateHandle: SavedStateHandle,
    private val playersListsRepository: PlayersListsRepository,
    private val playerManager: PlayerManager,
) : ViewModel() {

    //private val playerId: Int = checkNotNull(savedStateHandle[PlayersListDestination.playerIdArg])
    private val _uiState = MutableStateFlow(PlayersListsUiState())
    val uiState: StateFlow<PlayersListsUiState> = _uiState.asStateFlow()

    init{
        loadAllListsPlayersDetails()
    }

    fun addNewList(name: String){
        viewModelScope.launch {
            val playersList = PlayersList(name = name)
            playersListsRepository.insertPlayersList(playersList)
        }
    }

    private fun loadAllListsPlayersDetails() {
        viewModelScope.launch {
            // First, collect the list of PlayersList from the repository
            playersListsRepository.getAllPlayersListsStream()
                .collect { playersLists ->
                    // For each PlayersList, fetch the associated PlayerDetails concurrently
                    val updatedPlayersLists = playersLists.associateWith { playersList ->
                        val playersDetails =
                            playersListsRepository.getPlayersDetailsFromPlayersList(playersList.id)
                        playersDetails
                    }

                    // Update the UI state after fetching all PlayerDetails for all lists
                    _uiState.update { currentState ->
                        currentState.copy(playersLists = updatedPlayersLists)
                    }
                }
        }
    }

}

