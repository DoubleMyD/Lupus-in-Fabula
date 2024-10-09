package com.example.lupusinfabulav1.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lupusinfabulav1.LupusInFabulaApplication
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.data.entity.Player
import com.example.lupusinfabulav1.model.MostVotedPlayer
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.RoleVotes
import com.example.lupusinfabulav1.model.VoteManager
import com.example.lupusinfabulav1.model.toPlayerDetails
import com.example.lupusinfabulav1.ui.VillageUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
    data class AssassinKilledPlayers(val playerDetailsKilled: PlayerDetails) : RoleTypeEvent()
    data class CupidoKilledPlayers(val playersKilled: Pair<PlayerDetails, PlayerDetails>) : RoleTypeEvent()
    data class FaciliCostumiSavedPlayer(val playerDetailsSaved: PlayerDetails) : RoleTypeEvent()
    data class VeggenteDiscoverKiller(val killer: PlayerDetails) : RoleTypeEvent()
}

class VillageViewModel(
    private val playerManager: PlayerManager,
    private val voteManager: VoteManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VillageUiState())
    val uiState: StateFlow<VillageUiState> = _uiState.asStateFlow()

    // Event channel for UI interactions like Toasts or Dialogs
//    private val _uiEvent = MutableSharedFlow<VillageEvent>()
//    val uiEvent: SharedFlow<VillageEvent> = _uiEvent.asSharedFlow()

    // Using StateFlow for events (not ideal)
    private val _uiEvent = MutableStateFlow<VillageEvent?>(null)
    val uiEvent: StateFlow<VillageEvent?> = _uiEvent.asStateFlow()

    //private val voteManager = VoteManager()

    private val roles = Role.entries.toMutableList()
    private var roleIndex = 0

    init {
        viewModelScope.launch {
            //this allows to reflect changes in the players in PlayerManager in the uiState
            playerManager.players.collect { players ->
                _uiState.value = _uiState.value.copy(playerDetails = players)
            }
        }
    }

    fun nextRole() {
        if (!_uiState.value.gameStarted) {
            startVoting()
            return
        }

        val mostVotedPlayer = voteManager.getMostVotedPlayer()
        if (mostVotedPlayer != null) {
            if (_uiState.value.currentRole == Role.CITTADINO) {
                val mostCittadinoVotedPlayer = mostVotedPlayer as MostVotedPlayer.SinglePlayer
                updateKilledPlayer(listOf(mostCittadinoVotedPlayer.playerDetails))
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
            currentState.copy(gameStarted = true)
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

    fun vote(voter: PlayerDetails, votedPlayerDetails: PlayerDetails) {
        if (_uiState.value.gameStarted) {
            if (!votedPlayerDetails.alive) {
                return
            }
            if (voteManager.isLastVote()) {
                triggerAndClearEvent(VillageEvent.AllPlayersHaveVoted)
                return
            }
            if(voteManager.getLastVotingState().role == Role.CUPIDO){
                if(voteManager.getLastVotingState().votesPairPlayers.any { it.voter == voter && it.votedPlayerDetails == votedPlayerDetails }) {
                    triggerAndClearEvent(VillageEvent.CupidoAlreadyVoted)
                    return
                }
            }

            voteManager.vote(voter = voter, votedPlayerDetails = votedPlayerDetails)

            val votingState = voteManager.getLastVotingState()

            if (_uiState.value.currentRole == Role.MEDIUM) {
                updatePlayerRole(voter, votedPlayerDetails.role)
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
                cupidoVotedPlayers.playerDetails1 == assasinVotedPlayer.playerDetails || cupidoVotedPlayers.playerDetails2 == assasinVotedPlayer.playerDetails

            if (cupidoKilled) {
                handleCupidoKilled(cupidoVotedPlayers)
            } else {
                handleAssassinKilled(assasinVotedPlayer)
            }
        } else {
            triggerAndClearEvent(
                VillageEvent.RoleEvent(
                    RoleTypeEvent.FaciliCostumiSavedPlayer(assasinVotedPlayer.playerDetails)
                )
            )
        }

        val veggentePlayer = _uiState.value.playerDetails.filter { it.role == Role.VEGGENTE && it.alive }
        if (veggentePlayer.isNotEmpty()) {
            val veggenteVotedPlayer = votedPlayerByRole[Role.VEGGENTE] as MostVotedPlayer.SinglePlayer
            if(veggenteVotedPlayer.playerDetails.role == Role.ASSASSINO) {
                handleVeggenteDiscover(veggenteVotedPlayer)
            }
        }

        val winner = checkWinCondition()
        when(winner){
            Role.ASSASSINO -> updateIsGameFinished(_uiState.value.playerDetails.filter { it.role == Role.ASSASSINO })
            Role.CITTADINO -> updateIsGameFinished(_uiState.value.playerDetails.filter { it.role != Role.ASSASSINO })
            else -> {}
        }
    }

    private fun checkWinCondition(): Role?{
        val playersAlive = _uiState.value.playerDetails.filter { it.alive }
        val assassinPlayer = playersAlive.filter { it.role == Role.ASSASSINO }
        val otherPlayers = playersAlive.filter { it.role != Role.ASSASSINO }

        if(assassinPlayer.isEmpty())
            return Role.CITTADINO
        if(otherPlayers.size < assassinPlayer.size)
            return Role.ASSASSINO
        return null
    }
    
    private fun handleCupidoKilled(cupidoVotedPlayers: MostVotedPlayer.PairPlayers){
        val cupidoKilledPlayersEvent = RoleTypeEvent.CupidoKilledPlayers(
            Pair(
                cupidoVotedPlayers.playerDetails1,
                cupidoVotedPlayers.playerDetails2
            )
        )
        updateKilledPlayer(listOf(cupidoVotedPlayers.playerDetails1, cupidoVotedPlayers.playerDetails2))

        triggerAndClearEvent(VillageEvent.RoleEvent(cupidoKilledPlayersEvent))
        triggerAndClearEvent(VillageEvent.RoleEvent(cupidoKilledPlayersEvent))
    }

    private fun handleAssassinKilled(assasinVotedPlayer: MostVotedPlayer.SinglePlayer){
        updateKilledPlayer(listOf(assasinVotedPlayer.playerDetails))

        triggerAndClearEvent(
            VillageEvent.RoleEvent(
                RoleTypeEvent.AssassinKilledPlayers(assasinVotedPlayer.playerDetails)
            )
        )
    }

    private fun handleVeggenteDiscover( veggenteVotedPlayer: MostVotedPlayer.SinglePlayer){
        triggerAndClearEvent(
            VillageEvent.RoleEvent(
                RoleTypeEvent.VeggenteDiscoverKiller(veggenteVotedPlayer.playerDetails)
            )
        )
    }


    private fun startNewVoting(votingRole: Role) {
        val voterPlayers = when (votingRole) {
            Role.CITTADINO -> _uiState.value.playerDetails.filter { it.alive }
            else -> _uiState.value.playerDetails.filter { it.role == votingRole && it.alive }
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
        roleIndex = (++roleIndex) % roles.size
        return roles[roleIndex]
    }

    private fun updateKilledPlayer(listOfKilledPlayerDetails: List<PlayerDetails>) {
        val updatedPlayers = _uiState.value.playerDetails.map { player ->
            if (listOfKilledPlayerDetails.contains(player)) {
                player.copy(alive = false)
            }
            else
                player
        }
        _uiState.update { currentState ->
            currentState.copy(playerDetails = updatedPlayers)
        }
    }
    private fun updatePlayerRole(playerDetailsToUpdate: PlayerDetails, newRole: Role) {
        _uiState.update { currentState ->
            val updatedPlayers = currentState.playerDetails.map { player ->
                if (player == playerDetailsToUpdate) {
                    player.changeRole(newRole)
                } else {
                    player
                }
            }
            currentState.copy(playerDetails = updatedPlayers)
        }
    }
    
    private fun updateIsGameFinished(winnerPlayerDetails: List<PlayerDetails>){
        _uiState.update { currentState ->
            currentState.copy(isGameFinished = true)
        }
        updateWinnerPlayers(winnerPlayerDetails)
    }
    
    private fun updateWinnerPlayers(winnerPlayerDetails: List<PlayerDetails>){
        _uiState.update { currentState ->
            currentState.copy(winnerPlayerDetails = winnerPlayerDetails)
        }
    }

    private fun updateCurrentRole(role: Role) {
        _uiState.update { currentState ->
            currentState.copy(currentRole = role)
        }
    }

    private fun updateSelectedPlayer(playerDetails: PlayerDetails) {
        _uiState.update { currentState ->
            currentState.copy(selectedPlayerDetails = playerDetails)
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
        if (_uiState.value.round == 1 || _uiState.value.round == 3) {
            roles.remove(Role.MEDIUM)
        } else if (_uiState.value.round == 2) {
            roles.remove(Role.CUPIDO)
            roles.add(Role.MEDIUM)
        }
    }

    private fun resetVotedPlayerByRole() {
        _uiState.update { currentState ->
            currentState.copy(votedPlayerByRole = currentState.votedPlayerByRole.filter { it.key == Role.CUPIDO })
        }
    }

    // Factory for Dependency Injection
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LupusInFabulaApplication)
                val playerManager = application.container.playerManager
                val voteManager = application.container.voteManager
                VillageViewModel(playerManager = playerManager, voteManager)
            }
        }
    }
}