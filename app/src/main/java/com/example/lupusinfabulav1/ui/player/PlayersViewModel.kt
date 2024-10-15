package com.example.lupusinfabulav1.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


data class DatabasePlayersUiState(
    val playersDetails: List<PlayerDetails> = emptyList(),
)

class PlayersViewModel(
    private val playersRepository: PlayersRepository
) : ViewModel(){

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

    companion object {
        private const val TIMEOUT_MILLIS = 10_000L
    }
}