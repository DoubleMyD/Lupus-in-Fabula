package com.example.lupusinfabulav1.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.model.MostVotedPlayer
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.RoleVotes
import com.example.lupusinfabulav1.model.RoundResultEvent
import com.example.lupusinfabulav1.model.VoteManager
import com.example.lupusinfabulav1.model.RoundResultManager
import com.example.lupusinfabulav1.ui.GameState
import com.example.lupusinfabulav1.ui.VillageUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//private const val TAG = "VillageViewModel"

// Define possible UI events, such as showing a message
sealed class VillageEvent {
    data object NullEvent : VillageEvent()
    data object ErrorNotAllPlayersHaveVoted : VillageEvent()
    data object VotedPlayerIsDead : VillageEvent()
    data object GameNotStarted : VillageEvent()
    data object AllPlayersHaveVoted : VillageEvent()
    data object Tie : VillageEvent()
    data object TieRestartVoting : VillageEvent()
    data object CupidoAlreadyVoted : VillageEvent()
    //data class RoleEvent(val roleEvent: RoleTypeEvent) : VillageEvent()
}
//
//sealed class RoleTypeEvent {
//    data class AssassinKilledPlayers(val playerDetailsKilled: PlayerDetails) : RoleTypeEvent()
//    data class CupidoKilledPlayers(val playersKilled: Pair<PlayerDetails, PlayerDetails>) :
//        RoleTypeEvent()
//
//    data class FaciliCostumiSavedPlayer(val playerDetailsSaved: PlayerDetails) : RoleTypeEvent()
//    data class VeggenteDiscoverKiller(val killer: PlayerDetails) : RoleTypeEvent()
//}

class VillageViewModel(
    private val playerManager: PlayerManager,
    private val voteManager: VoteManager,
    private val roundResultManager: RoundResultManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(VillageUiState())
    val uiState: StateFlow<VillageUiState> = _uiState.asStateFlow()

    // Using StateFlow for events (not ideal)
    private val _uiEvent = MutableStateFlow<VillageEvent?>(null)
    val uiEvent: StateFlow<VillageEvent?> = _uiEvent.asStateFlow()

    private val _roundResultEvent = MutableStateFlow<List<RoundResultEvent>>(emptyList())
    val roundResultEvent: StateFlow<List<RoundResultEvent>> = _roundResultEvent.asStateFlow()

    private val roles = Role.entries.toMutableList()
    private var roleIndex = 0

    init {
        viewModelScope.launch {
            //this allows to reflect changes in the players in PlayerManager in the uiState
            playerManager.players.collect { players ->
                _uiState.update { currentState ->
                    currentState.copy(playersState = currentState.playersState.copy(playersDetails = players))
                }
            }
        }
    }

    private fun startGame() {
        _uiState.update { currentState ->
            currentState.copy(
                gameState = GameState.InProgress(round = 0, currentRole = Role.CITTADINO)
            )
        }
        goToNextRole()
    }

    fun nextRole() {
        val currentGameState = _uiState.value.gameState

        // If the game hasn't started, initiate the voting process
        if (currentGameState is GameState.NotStarted) {
            startGame()
            return
        }

        if (currentGameState is GameState.InProgress) {
            val mostVotedPlayer = voteManager.getMostVotedPlayer()
            if (mostVotedPlayer != null) {
                // Update the voted player for the current role
                handleVotedPlayer(currentGameState.currentRole, mostVotedPlayer)
                goToNextRole()
            } else {
                if (voteManager.isLastVote()) {
                    handleLastVote(currentGameState.currentRole)
                } else {
                    triggerAndClearEvent(VillageEvent.ErrorNotAllPlayersHaveVoted)

                }
            }

        }
    }

    private fun handleLastVote(currentRole: Role) {
        if (currentRole == Role.MEDIUM) {
            goToNextRole()
        } else {
            startNewVoting(currentRole)
            triggerAndClearEvent(VillageEvent.Tie)
        }
    }

    // Handle a player being voted
    private fun handleVotedPlayer(currentRole: Role, mostVotedPlayer: MostVotedPlayer) {
        when (currentRole) {
            Role.CITTADINO -> {
                val mostCittadinoVotedPlayer = mostVotedPlayer as MostVotedPlayer.SinglePlayer
                updateKilledPlayer(listOf(mostCittadinoVotedPlayer.playerDetails))
            }

            else -> {
                updateVotedPlayerByRole(currentRole, mostVotedPlayer)
            }
        }
    }


    private fun goToNextRole() {
        //val currentGameState = _uiState.value.gameState as? GameState.InProgress ?: return
        val nextRole = getNextRole()

        updateCurrentRole(nextRole)

        when (nextRole) {
            Role.CITTADINO -> handleRoundVoteResult()
            Role.ASSASSINO -> updateRound()
            else -> {}
        }
        startNewVoting(nextRole)
    }

    fun vote(voter: PlayerDetails, votedPlayerDetails: PlayerDetails) {
        if (_uiState.value.gameState !is GameState.InProgress) {
            triggerAndClearEvent(VillageEvent.GameNotStarted)
            return
        }
        val isValidVote = voteManager.isValidVote(voter, votedPlayerDetails)
        if (isValidVote != VillageEvent.NullEvent) {
            triggerAndClearEvent(isValidVote)
            return
        }

        val gameState = _uiState.value.gameState as GameState.InProgress

        voteManager.vote(voter = voter, votedPlayerDetails = votedPlayerDetails)

        val votingState = voteManager.getLastVotingState()

        if (gameState.currentRole == Role.MEDIUM) {
            updatePlayerRole(voter, votedPlayerDetails.role)
        } else {
            updateCurrentVoting(votingState)
        }

        if (voteManager.isLastVote()) {
            return
        }

        val newSelectedPlayer = voteManager.getNextVoter()
        updateSelectedPlayer(newSelectedPlayer)
    }

    private fun handleRoundVoteResult() {
        val votedPlayerByRole = _uiState.value.vote.votedPlayerByRole
        val playersDetails = _uiState.value.playersState.playersDetails
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
        /*if(voteResultManager.faciliCostumiSavedPlayer(votedPlayerByRole)){
            val savedPlayer = votedPlayerByRole[Role.FACILI_COSTUMI] as MostVotedPlayer.SinglePlayer
            triggerAndClearEvent(VillageEvent.RoleEvent(RoleTypeEvent.FaciliCostumiSavedPlayer(savedPlayer.playerDetails)))
        } else {
            if(voteResultManager.killedPlayerIsCupido(votedPlayerByRole)){
                val cupidoVotedPlayers = votedPlayerByRole[Role.CUPIDO] as MostVotedPlayer.PairPlayers
                handleCupidoKilled(cupidoVotedPlayers = cupidoVotedPlayers)
            } else {
                val assassinVotedPlayer = votedPlayerByRole[Role.ASSASSINO] as MostVotedPlayer.SinglePlayer
                handleAssassinKilled(assasinVotedPlayer = assassinVotedPlayer)
            }
        }

        if(voteResultManager.veggenteDiscoverKiller(_uiState.value.playersState.playersDetails, votedPlayerByRole)){
            val veggenteVotedPlayer = votedPlayerByRole[Role.VEGGENTE] as MostVotedPlayer.SinglePlayer
            handleVeggenteDiscover(veggenteVotedPlayer = veggenteVotedPlayer)
        }*/

        /*val assasinVotedPlayer = votedPlayerByRole[Role.ASSASSINO] as MostVotedPlayer.SinglePlayer
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

        val veggentePlayer =
            _uiState.value.playersState.playersDetails.filter { it.role == Role.VEGGENTE && it.alive }
        if (veggentePlayer.isNotEmpty()) {
            val veggenteVotedPlayer =
                votedPlayerByRole[Role.VEGGENTE] as MostVotedPlayer.SinglePlayer
            if (veggenteVotedPlayer.playerDetails.role == Role.ASSASSINO) {
                handleVeggenteDiscover(veggenteVotedPlayer)
            }
        }*/

        val voteResult = roundResultManager.getRoundVoteResult(
            playersDetails = playersDetails,
            votedPlayerByRole = votedPlayerByRole
        )
        updateKilledPlayer(killedPlayersDetails = voteResult.killedPlayers)
//        voteResult.events.forEach { event ->
//            triggerAndClearEvent(event)
//        }

        val winner = roundResultManager.getWinners(_uiState.value.playersState.playersDetails)
        when (winner) {
            Role.ASSASSINO -> updateIsGameFinished(_uiState.value.playersState.playersDetails.filter { it.role == Role.ASSASSINO })
            Role.CITTADINO -> updateIsGameFinished(_uiState.value.playersState.playersDetails.filter { it.role != Role.ASSASSINO })
            else -> triggerRoundResultEvent(voteResult.events)
        }
    }

    /*private fun handleCupidoKilled(cupidoVotedPlayers: MostVotedPlayer.PairPlayers) {
        val cupidoKilledPlayersEvent = RoleTypeEvent.CupidoKilledPlayers(
            Pair(
                cupidoVotedPlayers.playerDetails1,
                cupidoVotedPlayers.playerDetails2
            )
        )
        updateKilledPlayer(
            listOf(
                cupidoVotedPlayers.playerDetails1,
                cupidoVotedPlayers.playerDetails2
            )
        )

        triggerAndClearEvent(VillageEvent.RoleEvent(cupidoKilledPlayersEvent))
        triggerAndClearEvent(VillageEvent.RoleEvent(cupidoKilledPlayersEvent))
    }

    private fun handleAssassinKilled(assasinVotedPlayer: MostVotedPlayer.SinglePlayer) {
        updateKilledPlayer(listOf(assasinVotedPlayer.playerDetails))

        triggerAndClearEvent(
            VillageEvent.RoleEvent(
                RoleTypeEvent.AssassinKilledPlayers(assasinVotedPlayer.playerDetails)
            )
        )
    }

    private fun handleVeggenteDiscover(veggenteVotedPlayer: MostVotedPlayer.SinglePlayer) {
        triggerAndClearEvent(
            VillageEvent.RoleEvent(
                RoleTypeEvent.VeggenteDiscoverKiller(veggenteVotedPlayer.playerDetails)
            )
        )
    }*/

    private fun startNewVoting(votingRole: Role) {
        val voterPlayers = when (votingRole) {
            Role.CITTADINO -> _uiState.value.playersState.playersDetails.filter { it.alive }
            else -> _uiState.value.playersState.playersDetails.filter { it.role == votingRole && it.alive }
        }
        voteManager.startVoting(votingRole, voterPlayers)
        updateSelectedPlayer(voterPlayers.first())
        updateCurrentVoting(voteManager.getLastVotingState())
    }

    // Function to trigger an event and clear it after a delay
    private fun triggerAndClearEvent(event: VillageEvent) {
        val delayMillis: Long = 7500
        viewModelScope.launch {
            _uiEvent.value = event  // Emit the event
            delay(delayMillis)             // Delay to allow UI to handle the event (e.g., Toast duration)
            _uiEvent.value = null   // Clear the event
        }
    }

    private fun triggerRoundResultEvent(events: List<RoundResultEvent>) {
        viewModelScope.launch {
            _roundResultEvent.value = events
        }
    }

    private fun getNextRole(): Role {
        roleIndex = (roleIndex + 1) % roles.size
        return roles[roleIndex]
    }

    private fun updateKilledPlayer(killedPlayersDetails: List<PlayerDetails>) {
        val updatedPlayers = _uiState.value.playersState.playersDetails.map { player ->
            if (killedPlayersDetails.contains(player)) {
                player.copy(alive = false)
            } else
                player
        }
        _uiState.update { currentState ->
            currentState.copy(playersState = currentState.playersState.copy(playersDetails = updatedPlayers))
        }
    }

    private fun updatePlayerRole(playerDetailsToUpdate: PlayerDetails, newRole: Role) {
        val updatedPlayers = _uiState.value.playersState.playersDetails.map { player ->
            if (player == playerDetailsToUpdate) {
                player.copy(role = newRole)
            } else {
                player
            }
        }
        _uiState.update { currentState ->
            /*val updatedPlayers = currentState.playersState.playersDetails.map { player ->
                if (player == playerDetailsToUpdate) {
                    player.copy(role = newRole)
                } else {
                    player
                }
            }*/
            currentState.copy(playersState = currentState.playersState.copy(playersDetails = updatedPlayers))
        }
    }

    private fun updateIsGameFinished(winnerPlayerDetails: List<PlayerDetails>) {
        _uiState.update { currentState ->
            currentState.copy(gameState = GameState.Finished(winnerPlayerDetails))
        }
    }

    private fun updateCurrentRole(role: Role) {
        _uiState.update { currentState ->
            when (currentState.gameState) {
                is GameState.InProgress -> currentState.copy(
                    gameState = currentState.gameState.copy(currentRole = role)
                )

                else -> {
                    throw Exception("Invalid GameState")
                } // Handle other GameState cases if needed
            }
        }
    }

    private fun updateSelectedPlayer(playerDetails: PlayerDetails) {
        _uiState.update { currentState ->
            currentState.copy(playersState = currentState.playersState.copy(selectedPlayer = playerDetails))
        }
    }

    private fun updateVotedPlayerByRole(role: Role, votedPlayer: MostVotedPlayer) {
        val votedPlayerByRole = role to votedPlayer
        _uiState.update { currentState ->
            currentState.copy(vote = currentState.vote.copy(votedPlayerByRole = currentState.vote.votedPlayerByRole + votedPlayerByRole))
        }
    }

    private fun updateCurrentVoting(votingState: RoleVotes) {
        _uiState.update { currentState ->
            currentState.copy(vote = currentState.vote.copy(currentVoting = votingState))
        }
    }

    private fun updateRound() {
        _uiState.update { currentState ->
            when (currentState.gameState) {
                is GameState.InProgress -> currentState.copy(
                    gameState = currentState.gameState.copy(round = currentState.gameState.round + 1)
                )

                else -> {
                    throw Exception("Invalid GameState")
                } // Handle other GameState cases if needed
            }
        }
        updateRoleToVote()
        resetVotedPlayerByRole()
    }

    private fun updateRoleToVote() {
        when (val gameState = _uiState.value.gameState) {
            is GameState.InProgress -> {
                if (gameState.round == 1 || gameState.round == 3) {
                    roles.remove(Role.MEDIUM)
                } else if (gameState.round == 2) {
                    roles.remove(Role.CUPIDO)
                    roles.add(Role.MEDIUM)
                }

                roles.removeAll { role ->
                    role != Role.CITTADINO && _uiState.value.playersState.playersDetails.filter { it.role == role }
                        .all { !it.alive }
                }
            }

            else -> throw Exception("Invalid GameState")
        }
    }

    private fun resetVotedPlayerByRole() {
        _uiState.update { currentState ->
            currentState.copy(vote = currentState.vote.copy(votedPlayerByRole = currentState.vote.votedPlayerByRole.filter { it.key == Role.CUPIDO }))
        }
    }
}