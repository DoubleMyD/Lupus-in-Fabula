package com.example.lupusinfabulav1.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PlayersUiState(
    val playersDetails: List<PlayerDetails> = emptyList(),
)
class PlayersViewModel(
    private val playersRepository: PlayersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayersUiState())
    val uiState: StateFlow<PlayersUiState> = _uiState.asStateFlow()

    val databasePlayers: StateFlow<PlayersUiState> =
        playersRepository.getAllPlayersStream().map { players ->
            PlayersUiState(
                playersDetails = players.map { it.toPlayerDetails() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PlayersUiState()
        )


    companion object {
        private const val TIMEOUT_MILLIS = 10_000L
    }
}