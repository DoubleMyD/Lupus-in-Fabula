package com.example.lupusinfabulav1.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.VillageUiState
import com.example.lupusinfabulav1.model.voting.RoleVotes
import com.example.lupusinfabulav1.model.voting.VoteManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
    data object TieRestartVoting: VillageEvent()
}

class VillageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VillageUiState())
    val uiState: StateFlow<VillageUiState> = _uiState.asStateFlow()

    // Event channel for UI interactions like Toasts or Dialogs
//    private val _uiEvent = MutableSharedFlow<VillageEvent>()
//    val uiEvent: SharedFlow<VillageEvent> = _uiEvent.asSharedFlow()

    // Using StateFlow for events (not ideal)
    private val _uiEvent = MutableStateFlow<VillageEvent?>(null)
    val uiEvent: StateFlow<VillageEvent?> = _uiEvent.asStateFlow()

    private val voteManager = VoteManager()

    private val roles = Role.entries.toMutableList()
    private var roleIndex = 0

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
//                Log.d(TAG, "Most voted player: $mostVotedPlayer")
                updateVotedPlayerByRole(_uiState.value.currentRole, mostVotedPlayer)
                goToNextRole()
            } else {
                if(voteManager.isLastVote()) {
//                    Log.d(TAG, "Tie")
                    startNewVoting(_uiState.value.currentRole)
                    triggerAndClearEvent(VillageEvent.Tie)
                }
                else{
                    triggerAndClearEvent(VillageEvent.ErrorNotAllPlayersHaveVoted)
                }
            }
        } else {
            startVoting()
        }
    }

    private fun startVoting() {
        _uiState.update { currentState ->
            currentState.copy(isGameStarted = true)
        }
        goToNextRole()
    }

    private fun goToNextRole() {
        val nextRole = getNextRole()
        updateCurrentRole(nextRole)
//        Log.d(TAG, "Next role: $nextRole")

        startNewVoting(nextRole)
        if(nextRole == Role.ASSASSINO) {
            updateRound()
        }
    }

    fun vote(voter: Player, votedPlayer: Player) {
        if (_uiState.value.isGameStarted) {
            if (!votedPlayer.alive) {
                return
            }
            if ( voteManager.isLastVote()) {
                triggerAndClearEvent(VillageEvent.AllPlayersHaveVoted)
                Log.d(TAG, "All players have voted")
                return
            }
            voteManager.vote(voter = voter, votedPlayer = votedPlayer)

            val votingState = voteManager.getLastVotingState()
            updateCurrentVoting(votingState)

            if (voteManager.isLastVote()) {
                return
            }

            val newSelectedPlayer = voteManager.getNextVoter()
            updateSelectedPlayer(newSelectedPlayer)
        } else {
            triggerAndClearEvent(VillageEvent.GameNotStarted)
        }
    }

    // Function to trigger an event and clear it after a delay
    private fun triggerAndClearEvent(event: VillageEvent) {
        viewModelScope.launch {
            _uiEvent.value = event  // Emit the event
            delay(7500)             // Delay to allow UI to handle the event (e.g., Toast duration)
            _uiEvent.value = null   // Clear the event
        }
    }

    private fun startNewVoting(votingRole: Role) {
        val voterPlayers = _uiState.value.players.filter { it.role == votingRole && it.alive }
        voteManager.startVoting(votingRole, voterPlayers)

        updateSelectedPlayer(voterPlayers.first())
        updateCurrentVoting(voteManager.getLastVotingState())
    }

    private fun getNextRole(): Role {
        val nextIndex = (++roleIndex) % roles.size
        return roles[nextIndex]
    }

    private fun updateCurrentRole(role: Role) {
        _uiState.update { currentState ->
            currentState.copy(currentRole = role)
        }
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

    private fun updateRound() {
        _uiState.update { currentState ->
            currentState.copy(round = currentState.round + 1)
        }
        updateRoleToVote()
    }

    private fun updateRoleToVote() {
        Log.d(TAG, "Round: ${_uiState.value.round}")
        if (_uiState.value.round == 1 || _uiState.value.round == 3) {
            roles.remove(Role.MEDIUM)
        } else if (_uiState.value.round == 2) {
            roles.remove(Role.CUPIDO)
            roles.add(Role.MEDIUM)
        }
    }
}