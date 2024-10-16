package com.example.lupusinfabulav1.ui.playersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditPlayersListUiState(
    val listId: Int = -1,
    val playersList: PlayersList? = null,
    val playersDetails: List<PlayerDetails> = emptyList(),
)

class EditPlayersListViewModel(
    private val playersListsRepository: PlayersListsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPlayersListUiState())
    val uiState: StateFlow<EditPlayersListUiState> = _uiState.asStateFlow()

    fun loadPlayersDetailsFromPlayersList(listId: Int) {
        viewModelScope.launch {
            // Collect PlayersList from repository
            val playersDetails = playersListsRepository.getPlayersDetailsFromPlayersList(listId)
            val playersList = playersListsRepository.getPlayersListStream(listId).first()
            // Update the UI state with the new data
            _uiState.update { currentState ->
                currentState.copy(
                    listId = listId,
                    playersList = playersList,
                    playersDetails = playersDetails
                )
            }
        }
    }
}
