package com.example.lupusinfabulav1.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayersListUiState(
    val playersDetails: List<PlayerDetails> = emptyList()
)

class PlayersListViewModel(
    savedStateHandle: SavedStateHandle,
    private val playersRepository: PlayersRepository
) : ViewModel() {

    //private val playerId: Int = checkNotNull(savedStateHandle[PlayersListDestination.playerIdArg])

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<PlayersListUiState> =
        playersRepository.getAllPlayersStream().map { players -> PlayersListUiState(players.map { it.toPlayerDetails() }) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PlayersListUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}