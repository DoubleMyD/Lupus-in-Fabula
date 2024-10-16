package com.example.lupusinfabulav1.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DatabasePlayersUiState(
    val playersDetails: List<PlayerDetails> = emptyList(),
)

data class PlayersEditListUiState(
    val listId: Int = -1,
    val selectedPlayers: Set<PlayerDetails> = emptySet(),
    val selectedPlayersId: Set<Int> = emptySet(),
)

class PlayersEditListViewModel(
    private val playersRepository: PlayersRepository,
    private val playersListsRepository: PlayersListsRepository,
) : ViewModel() {

    val databasePlayersUiState: StateFlow<DatabasePlayersUiState> =
        playersRepository.getAllPlayersStream().map { players ->
            DatabasePlayersUiState(
                playersDetails = players.map { it.toPlayerDetails() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = DatabasePlayersUiState()
        )

    private val _uiState = MutableStateFlow(PlayersEditListUiState())
    val uiState: StateFlow<PlayersEditListUiState> = _uiState.asStateFlow()

    fun updateSelectedPlayers(selectedPlayer: PlayerDetails) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedPlayers= if (selectedPlayer in currentState.selectedPlayers) {
                    currentState.selectedPlayers - selectedPlayer // Remove if present
                } else {
                    currentState.selectedPlayers + selectedPlayer // Add if not present
                }
            )
        }
    }

    fun updatePlayersIdOfList(listId: Int, addedPlayers: List<Int>, removedPlayers: List<Int>) {
        viewModelScope.launch {
            playersListsRepository.updatePlayersIdOfList(
                listId = listId,
                addedPlayers = addedPlayers,
                removedPlayers = removedPlayers
            )
        }
    }

    fun initScreen(listId: Int, playersIdList: List<Int>) {
        _uiState.update { currentState ->
            currentState.copy(
                listId = listId,
                selectedPlayersId = playersIdList.toSet()
            )
        }

        // Use coroutines to load player details concurrently
        viewModelScope.launch {
            // Launch concurrent player detail loading
            val playersDetails = playersIdList.map { playerId ->
                async { loadPlayerDetails(playerId) } // Use async for parallel execution
            }.awaitAll() // Wait for all players details to be fetched

            // Filter out null results (if any) and update the UI state in a single update
            val nonNullPlayerDetails = playersDetails.filterNotNull()

            _uiState.update { currentState ->
                currentState.copy(
                    selectedPlayers = currentState.selectedPlayers + nonNullPlayerDetails
                )
            }
        }
    }

    // Refactored function to load a single player's details
    private suspend fun loadPlayerDetails(playerId: Int): PlayerDetails? {
        // Get the player's details as a suspend function
        return playersRepository.getPlayerStream(playerId).first()?.toPlayerDetails()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 10_000L
    }
}
