package com.example.lupusinfabulav1.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.data.VillageUiState
import com.example.lupusinfabulav1.model.voting.MostVotedPlayer
import com.example.lupusinfabulav1.model.voting.RoleVotes
import com.example.lupusinfabulav1.model.voting.VoteManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "VillageViewModel"

// Define possible UI events, such as showing a message
sealed class VillageEvent {
    data object ErrorNotAllPlayersHaveVoted : VillageEvent()
    data object GameNotStarted : VillageEvent()
    data object AllPlayersHaveVoted : VillageEvent()
    data object Tie : VillageEvent()
    data object TieRestartVoting : VillageEvent()
    data object CupidoAlreadyVoted : VillageEvent()
    data class RoleEvent(val roleEvent: RoleTypeEvent) : VillageEvent()
}

sealed class RoleTypeEvent {
    data class AssassinKilledPlayers(val playerKilled: Player) : RoleTypeEvent()
    data class CupidoKilledPlayers(val playersKilled: Pair<Player, Player>) : RoleTypeEvent()
    data class FaciliCostumiSavedPlayer(val playerSaved: Player) : RoleTypeEvent()
    data class VeggenteDiscoverKiller(val killer: Player) : RoleTypeEvent()
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
        if (!_uiState.value.isGameStarted) {
            startVoting()
            return
        }

        val mostVotedPlayer = voteManager.getMostVotedPlayer()
        if (mostVotedPlayer != null) {
            if (_uiState.value.currentRole == Role.CITTADINO) {
                val mostCittadinoVotedPlayer = mostVotedPlayer as MostVotedPlayer.SinglePlayer
                updateKilledPlayer(listOf(mostCittadinoVotedPlayer.player))
            } else {
                updateVotedPlayerByRole(_uiState.value.currentRole, mostVotedPlayer)
            }
            goToNextRole()
        } else {
            if (voteManager.isLastVote()) {
                if (_uiState.value.currentRole == Role.MEDIUM) {
                    goToNextRole()
                } else {
                    startNewVoting(_uiState.value.currentRole)
                    triggerAndClearEvent(VillageEvent.Tie)
                }
            } else {
                triggerAndClearEvent(VillageEvent.ErrorNotAllPlayersHaveVoted)
            }
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

        if (nextRole == Role.CITTADINO) {
            handleRoundVoteResult()
        }
        if (nextRole == Role.ASSASSINO) {
            updateRound()
        }
        startNewVoting(nextRole)

    }

    fun vote(voter: Player, votedPlayer: Player) {
        if (_uiState.value.isGameStarted) {
            if (!votedPlayer.alive) {
                return
            }
            if (voteManager.isLastVote()) {
                triggerAndClearEvent(VillageEvent.AllPlayersHaveVoted)
                Log.d(TAG, "All players have voted")
                return
            }
            if(voteManager.getLastVotingState().role == Role.CUPIDO){
                if(voteManager.getLastVotingState().votesPairPlayers.any { it.voter == voter && it.votedPlayer == votedPlayer }) {
                    triggerAndClearEvent(VillageEvent.CupidoAlreadyVoted)
                    return
                }
            }

            voteManager.vote(voter = voter, votedPlayer = votedPlayer)

            val votingState = voteManager.getLastVotingState()

            if (_uiState.value.currentRole == Role.MEDIUM) {
                updatePlayerRole(voter, votedPlayer.role)
            } else {
                updateCurrentVoting(votingState)
            }

            if (voteManager.isLastVote()) {
                return
            }

            val newSelectedPlayer = voteManager.getNextVoter()
            updateSelectedPlayer(newSelectedPlayer)
        } else {
            triggerAndClearEvent(VillageEvent.GameNotStarted)
        }
    }

    private fun handleRoundVoteResult() {
        val votedPlayerByRole = _uiState.value.votedPlayerByRole

        /*
        THE ORDER IS IMPORTANT :
            1) controlli se il giocatore è stato salvato
                1.1) se il giocatore non è stato salvato
                    1.1.1) controlli se era stato votato da cupido
                    1.1.2)  uccidi il giocatore o i giocatori (Cupido)
                1.2) avvisi l'utente che è stato salvato un giocatore
            2) controlli se il veggente è vivo e ha sgamato un assassino
                2.1) se l'assassino è stato sgamato, notifica i veggenti

         */

        val assasinVotedPlayer = votedPlayerByRole[Role.ASSASSINO] as MostVotedPlayer.SinglePlayer
        if (votedPlayerByRole[Role.FACILI_COSTUMI] != votedPlayerByRole[Role.ASSASSINO]) {
            val cupidoVotedPlayers = votedPlayerByRole[Role.CUPIDO] as MostVotedPlayer.PairPlayers
            val cupidoKilled =
                cupidoVotedPlayers.player1 == assasinVotedPlayer.player || cupidoVotedPlayers.player2 == assasinVotedPlayer.player

            if (cupidoKilled) {
                cupidoVotedPlayers.player1.kill()
                cupidoVotedPlayers.player2.kill()
                val cupidoKilledPlayersEvent = RoleTypeEvent.CupidoKilledPlayers(
                    Pair(cupidoVotedPlayers.player1, cupidoVotedPlayers.player2)
                )
                updateKilledPlayer(listOf(cupidoVotedPlayers.player1, cupidoVotedPlayers.player2))

                triggerAndClearEvent(VillageEvent.RoleEvent(cupidoKilledPlayersEvent))
                triggerAndClearEvent(VillageEvent.RoleEvent(cupidoKilledPlayersEvent))
            } else {
                assasinVotedPlayer.player.kill()
                updateKilledPlayer(listOf(assasinVotedPlayer.player))

                triggerAndClearEvent(
                    VillageEvent.RoleEvent(
                        RoleTypeEvent.AssassinKilledPlayers(
                            assasinVotedPlayer.player
                        )
                    )

                )
            }
        } else {
            triggerAndClearEvent(
                VillageEvent.RoleEvent(
                    RoleTypeEvent.FaciliCostumiSavedPlayer(
                        assasinVotedPlayer.player
                    )
                )
            )
        }

        val veggentePlayer = _uiState.value.players.filter { it.role == Role.VEGGENTE && it.alive }
        if (veggentePlayer.isNotEmpty()) {
            val veggenteVotedPlayer =
                votedPlayerByRole[Role.VEGGENTE] as MostVotedPlayer.SinglePlayer
            triggerAndClearEvent(
                VillageEvent.RoleEvent(
                    RoleTypeEvent.VeggenteDiscoverKiller(
                        veggenteVotedPlayer.player
                    )
                )
            )
        }
    }


    private fun startNewVoting(votingRole: Role) {
        val voterPlayers = when (votingRole) {
            Role.CITTADINO -> _uiState.value.players.filter { it.alive }
            else -> _uiState.value.players.filter { it.role == votingRole && it.alive }
        }
        voteManager.startVoting(votingRole, voterPlayers)

        updateSelectedPlayer(voterPlayers.first())
        updateCurrentVoting(voteManager.getLastVotingState())
    }

    // Function to trigger an event and clear it after a delay
    private fun triggerAndClearEvent(event: VillageEvent) {
        val delayMillis : Long = when (event) {
            is VillageEvent.RoleEvent -> 100
            else -> 7500
        }
        viewModelScope.launch {
            _uiEvent.value = event  // Emit the event
            delay(delayMillis)             // Delay to allow UI to handle the event (e.g., Toast duration)
            _uiEvent.value = null   // Clear the event
        }
    }

    private fun getNextRole(): Role {
        val nextIndex = (++roleIndex) % roles.size
        return roles[nextIndex]
    }

    private fun updateKilledPlayer(listOfKilledPlayers: List<Player>) {
        val updatedPlayers = _uiState.value.players.map { player ->
            if (listOfKilledPlayers.contains(player)) {
                player.copy(alive = false)
            }
            else
                player
        }
        _uiState.update { currentState ->
            currentState.copy(players = updatedPlayers)
        }
    }
    private fun updatePlayerRole(playerToUpdate: Player, newRole: Role) {
        _uiState.update { currentState ->
            val updatedPlayers = currentState.players.map { player ->
                if (player == playerToUpdate) {
                    player.changeRole(newRole)
                } else {
                    player
                }
            }
            currentState.copy(players = updatedPlayers)
        }
    }

    private fun resetVotedPlayerByRole() {
        _uiState.update { currentState ->
            currentState.copy(votedPlayerByRole = emptyMap())
        }
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

    private fun updateVotedPlayerByRole(role: Role, votedPlayer: MostVotedPlayer) {
        val votedPlayerByRole = role to votedPlayer
        _uiState.update { currentState ->
            currentState.copy(votedPlayerByRole = currentState.votedPlayerByRole + votedPlayerByRole)
        }
    }

    private fun updateCurrentVoting(votingState: RoleVotes) {
        _uiState.update { currentState ->
            currentState.copy(currentVoting = votingState)
        }
    }

    private fun updateRound() {
        _uiState.update { currentState ->
            currentState.copy(round = currentState.round + 1)
        }
        updateRoleToVote()
        resetVotedPlayerByRole()
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