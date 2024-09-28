package com.example.lupusinfabulav1.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.VillageUiState
import com.example.lupusinfabulav1.model.voting.RoleVotes
import com.example.lupusinfabulav1.model.voting.VoteManager
import com.example.lupusinfabulav1.model.voting.VoteMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "VillageViewModel"
// Define possible UI events, such as showing a message
sealed class VillageEvent{
    data object ErrorNotAllPlayersHaveVoted: VillageEvent()
    data object GameNotStarted: VillageEvent()
    data object AllPlayersHaveVoted: VillageEvent()
    data object Tie: VillageEvent()
    data object Tie_Restart_Voting: VillageEvent()
}

class VillageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VillageUiState())
    val uiState: StateFlow<VillageUiState> = _uiState.asStateFlow()

    // Event channel for UI interactions like Toasts or Dialogs
    private val _uiEvent = MutableSharedFlow<VillageEvent>()
    val uiEvent: SharedFlow<VillageEvent> = _uiEvent.asSharedFlow()

    private val voteManager = VoteManager()

    private val roles = Role.entries
    private var roleIndex = 0

    private var voterIndex = 0

    init {
        viewModelScope.launch {
            PlayerManager.players.collect { players ->
                _uiState.value = _uiState.value.copy(players = players)
            }
        }
    }

    fun nextRole() {
        if (_uiState.value.isGameStarted) {
            val mostVotedPlayer = voteManager.getMostVotedPlayer()

            if (mostVotedPlayer != null) {
                updateVotedPlayerByRole(_uiState.value.currentRole, mostVotedPlayer)
                goToNextRole()
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(VillageEvent.Tie)
                }
            }
        } else {
            startNewVoting()
        }
    }

    private fun startNewVoting() {
        _uiState.update { currentState ->
            currentState.copy(isGameStarted = true)
        }
        goToNextRole()
    }

    private fun goToNextRole() {
        roleIndex = (roleIndex + 1) % roles.size

        _uiState.update { currentState ->
            currentState.copy(currentRole = roles[roleIndex])
        }

        voterIndex = 0
        val voterPlayers = _uiState.value.players.filter { it.role == roles[roleIndex] && it.alive }
        voteManager.startVoting(_uiState.value.currentRole, voterPlayers)

        updateSelectedPlayer(voterPlayers.first())
        updateCurrentVoting(voteManager.getLastVotingState())
    }

    fun vote(voter: Player, votedPlayer: Player) {
        if (_uiState.value.isGameStarted) {
            if (!votedPlayer.alive) {
                return
            }
            if (voteManager.isVotingFinished()) {
                viewModelScope.launch {
                    _uiEvent.emit(VillageEvent.AllPlayersHaveVoted)
                }
                return
            }
            val voteMessage = voteManager.vote(voter = voter, votedPlayer = votedPlayer)

            val votingState = voteManager.getLastVotingState()
            updateCurrentVoting(votingState)

            if (voteManager.isLastVote()) {
                if (voteMessage == VoteMessage.RESTART) {
                    viewModelScope.launch {
                        _uiEvent.emit(VillageEvent.Tie_Restart_Voting)
                    }
                    startNewVoting()
                }
                return
            }

            val newSelectedPlayer = getNextVoter(votingState.voters)
            updateSelectedPlayer(newSelectedPlayer)
        } else {
            viewModelScope.launch {
                _uiEvent.emit(VillageEvent.GameNotStarted)
            }
        }
    }

    private fun getNextVoter(voters: List<Player>): Player{
        val nextIndex = ++voterIndex
        Log.d(TAG, "Voters: ${voters}")
        Log.d(TAG, "nextIndex: ${nextIndex}")
        Log.d(TAG, "getNextPlayerr: ${voters[nextIndex]}")
        return voters[nextIndex]
    }

    private fun updateSelectedPlayer(player: Player) {
        _uiState.update { currentState ->
            currentState.copy(selectedPlayer = player)
        }
    }

    private fun updateVotedPlayerByRole(role: Role, votedPlayer: Player) {
        val votedPlayerByRole = role to votedPlayer
        _uiState.update { currentState ->
            currentState.copy(votedPlayerByRole = currentState.votedPlayerByRole + votedPlayerByRole)
        }
    }

    private fun updateCurrentVoting(votingState: RoleVotes){
        _uiState.update { currentState ->
            currentState.copy(currentVoting = votingState)
        }
    }

}