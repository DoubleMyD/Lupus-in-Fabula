package com.example.lupusinfabulav1.ui.playersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.toPlayerDetails
import com.example.lupusinfabulav1.ui.navigation.Destination
import com.example.lupusinfabulav1.ui.navigation.setup.Navigator
import com.example.lupusinfabulav1.ui.navigation.PlayerDestination
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

data class EditPlayersListUiState(
    val listId: Int = -1,
    //val selectedPlayers: Set<PlayerDetails> = emptySet(),
    val selectedPlayersId: List<Int> = emptyList(),
)

class EditPlayersListViewModel(
    private val navigator: Navigator,
    private val playersRepository: PlayersRepository,
    private val playersListsRepository: PlayersListsRepository,
) : ViewModel() {

    val databasePlayers: StateFlow<DatabasePlayersUiState> =
        playersRepository.getAllPlayersStream().map { players ->
            DatabasePlayersUiState(
                playersDetails = players.map { it.toPlayerDetails() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = DatabasePlayersUiState()
        )

    private val _uiState = MutableStateFlow(EditPlayersListUiState())
    val uiState: StateFlow<EditPlayersListUiState> = _uiState.asStateFlow()

    fun addNewPlayer() {
        viewModelScope.launch {
            navigator.navigate(
                destination = PlayerDestination.NewPlayer(
                    returnToHomeScreen = false,
                    returnToEditPlayersList = true,
                    playersListId =  uiState.value.listId,
                    playersId = uiState.value.selectedPlayersId,
                ),
                navOptions = {
                    popUpTo(Destination.HomePage) {
                        inclusive = true
                    }
                }
            )
        }
    }

    fun updateSelectedPlayers(selectedPlayer: PlayerDetails) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedPlayersId = if (selectedPlayer.id in currentState.selectedPlayersId) {
                    currentState.selectedPlayersId - selectedPlayer.id // Remove if present
                } else {
                    currentState.selectedPlayersId + selectedPlayer.id // Add if not present
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
                selectedPlayersId = playersIdList
            )
        }

//        // Use coroutines to load player details concurrently
//        viewModelScope.launch {
//            // Launch concurrent player detail loading
//            val playersDetails = playersIdList.map { playerId ->
//                async { loadPlayerDetails(playerId) } // Use async for parallel execution
//            }.awaitAll() // Wait for all players details to be fetched
//
//            // Filter out null results (if any) and update the UI state in a single update
//            val nonNullPlayerDetails = playersDetails.filterNotNull()
//
//            _uiState.update { currentState ->
//                currentState.copy(
//                    selectedPlayers = currentState.selectedPlayers + nonNullPlayerDetails
//                )
//            }
//        }
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
