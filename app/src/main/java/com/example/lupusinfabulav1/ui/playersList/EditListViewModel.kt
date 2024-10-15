package com.example.lupusinfabulav1.ui.playersList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.PlayersListsRepository
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditListUiState(
    val playersList: PlayersList? = null,
    val playersDetails: List<PlayerDetails> = emptyList(),
)

class EditListViewModel(
    savedStateHandle: SavedStateHandle,
    private val playersListsRepository: PlayersListsRepository,
    private val playersRepository: PlayersRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    // Retrieve the listId argument from the SavedStateHandle (passed via navigation)
    private val listId: String? = savedStateHandle[EditListDestination.listIdArg]

    private val _uiState = MutableStateFlow(EditListUiState())
    val uiState: StateFlow<EditListUiState> = _uiState.asStateFlow()

    init {
        // Fetch the players list and details if the listId is not null
        listId?.let {
            getPlayersDetailsFromPlayersList(it.toInt()) // Assuming listId is an Int
        }
    }

    fun getPlayersDetailsFromPlayersList(listId: Int) {
        viewModelScope.launch {
            // Collect PlayersList from repository
            playersListsRepository.getPlayersListStream(listId)
                .collect { playersList ->
                    // Fetch the player details based on the playersId in PlayersList
                    val playersDetails = playersList?.playersId?.map { playerId ->
                        playersRepository.getPlayerStream(playerId).first()?.toPlayerDetails()
                            ?: PlayerDetails(
                                "error", imageSource = PlayerImageSource.Resource(
                                    R.drawable.ic_launcher_foreground
                                )
                            )
                    } ?: emptyList()

                    // Update the UI state with the new data
                    _uiState.value = EditListUiState(
                        playersList = playersList,
                        playersDetails = playersDetails
                    )
                }
        }
    }

}