package com.example.lupusinfabulav1.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.toPlayerDetails
import com.example.lupusinfabulav1.ui.VillageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class DatabasePlayersListUiState(
    val playersDetails: List<PlayerDetails> = emptyList(),
)

data class PlayersListUiState(
    val selectedPlayers: List<PlayerDetails> = emptyList(),
)

class PlayersListViewModel(
    savedStateHandle: SavedStateHandle,
    private val playersRepository: PlayersRepository,
    private val playerManager: PlayerManager,
) : ViewModel() {

    //private val playerId: Int = checkNotNull(savedStateHandle[PlayersListDestination.playerIdArg])
    private val _uiState = MutableStateFlow(PlayersListUiState())
    val uiState: StateFlow<PlayersListUiState> = _uiState.asStateFlow()
    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */
    val databasePlayersUiState: StateFlow<DatabasePlayersListUiState> =
        playersRepository.getAllPlayersStream().map { players ->
            DatabasePlayersListUiState(
                playersDetails = players.map { it.toPlayerDetails() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = DatabasePlayersListUiState()
        )

    fun addPlayer(playerDetails: PlayerDetails) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedPlayers = if (currentState.selectedPlayers.contains(playerDetails)) {
                    currentState.selectedPlayers - playerDetails
                } else {
                    currentState.selectedPlayers + playerDetails
                })
        }
    }

    fun updatePlayerManager(){
        for (playerDetails in _uiState.value.selectedPlayers) {
            playerManager.addPlayer(playerDetails)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

