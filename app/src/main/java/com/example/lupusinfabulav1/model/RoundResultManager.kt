package com.example.lupusinfabulav1.model

import com.example.lupusinfabulav1.ui.game.RoleTypeEvent
import com.example.lupusinfabulav1.ui.game.VillageEvent

data class RoundResult(
    val events: List<VillageEvent.RoleEvent>,
    val killedPlayers: List<PlayerDetails>
)

class RoundResultManager {
    private val roundResultHistory = mutableListOf<RoundResult>()

    fun getRoundVoteResult(
        playersDetails: List<PlayerDetails>,
        votedPlayerByRole: Map<Role, MostVotedPlayer>
    ): RoundResult {
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

        var roundResult = RoundResult(emptyList(), emptyList())

        if (faciliCostumiSavedPlayer(votedPlayerByRole)) {
            val savedPlayer = votedPlayerByRole[Role.FACILI_COSTUMI] as MostVotedPlayer.SinglePlayer
            roundResult = roundResult.copy(
                events = roundResult.events + VillageEvent.RoleEvent(
                    RoleTypeEvent.FaciliCostumiSavedPlayer(savedPlayer.playerDetails)
                )
            )
        } else {
            if (killedPlayerIsCupido(votedPlayerByRole)) {
                val cupidoVotedPlayers =
                    votedPlayerByRole[Role.CUPIDO] as MostVotedPlayer.PairPlayers
                val result = handleCupidoKilled(cupidoVotedPlayers = cupidoVotedPlayers)
                roundResult = roundResult.copy(
                    events = roundResult.events + result.events,
                    killedPlayers = roundResult.killedPlayers + result.killedPlayers
                )
            } else {
                val assassinVotedPlayer =
                    votedPlayerByRole[Role.ASSASSINO] as MostVotedPlayer.SinglePlayer
                val result = handleAssassinKilled(assasinVotedPlayer = assassinVotedPlayer)
                roundResult = roundResult.copy(
                    events = roundResult.events + result.events,
                    killedPlayers = roundResult.killedPlayers + result.killedPlayers
                )
            }
        }

        if (veggenteDiscoverKiller(playersDetails, votedPlayerByRole)) {
            val veggenteVotedPlayer =
                votedPlayerByRole[Role.VEGGENTE] as MostVotedPlayer.SinglePlayer
            val result = handleVeggenteDiscover(veggenteVotedPlayer = veggenteVotedPlayer)
            roundResult = roundResult.copy(
                events = roundResult.events + result.events,
                killedPlayers = roundResult.killedPlayers + result.killedPlayers
            )
        }

        roundResultHistory.add(roundResult)
        return roundResult

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
    }

    private fun handleCupidoKilled(cupidoVotedPlayers: MostVotedPlayer.PairPlayers): RoundResult {
        val cupidoKilledPlayersEvent = RoleTypeEvent.CupidoKilledPlayers(
            Pair(
                cupidoVotedPlayers.playerDetails1,
                cupidoVotedPlayers.playerDetails2
            )
        )
        return RoundResult(
            events = listOf(VillageEvent.RoleEvent(cupidoKilledPlayersEvent)),
            killedPlayers = listOf(
                cupidoVotedPlayers.playerDetails1,
                cupidoVotedPlayers.playerDetails2
            )
        )
    }

    private fun handleAssassinKilled(assasinVotedPlayer: MostVotedPlayer.SinglePlayer): RoundResult {
        return RoundResult(
            events = listOf(
                VillageEvent.RoleEvent(
                    RoleTypeEvent.AssassinKilledPlayers(assasinVotedPlayer.playerDetails)
                )
            ),
            killedPlayers = listOf(assasinVotedPlayer.playerDetails)
        )
    }

    private fun handleVeggenteDiscover(veggenteVotedPlayer: MostVotedPlayer.SinglePlayer): RoundResult {
        return RoundResult(
            events = listOf(
                VillageEvent.RoleEvent(
                    RoleTypeEvent.VeggenteDiscoverKiller(veggenteVotedPlayer.playerDetails)
                )
            ),
            killedPlayers = emptyList()
        )
    }

    fun getWinners(playersDetails: List<PlayerDetails>): Role? {
        val playersAlive = playersDetails.filter { it.alive }
        val assassinPlayer = playersAlive.filter { it.role == Role.ASSASSINO }
        val otherPlayers = playersAlive.filter { it.role != Role.ASSASSINO }

        if (assassinPlayer.isEmpty())
            return Role.CITTADINO
        if (otherPlayers.size < assassinPlayer.size)
            return Role.ASSASSINO
        return null
    }

    private fun faciliCostumiSavedPlayer(votedPlayerByRole: Map<Role, MostVotedPlayer>): Boolean {
        return votedPlayerByRole[Role.FACILI_COSTUMI] == votedPlayerByRole[Role.ASSASSINO]
    }

    private fun killedPlayerIsCupido(votedPlayerByRole: Map<Role, MostVotedPlayer>): Boolean {
        val cupidoVotedPlayers = votedPlayerByRole[Role.CUPIDO] as MostVotedPlayer.PairPlayers
        val assassinVotedPlayer = votedPlayerByRole[Role.ASSASSINO] as MostVotedPlayer.SinglePlayer
        val cupidoKilled =
            cupidoVotedPlayers.playerDetails1 == assassinVotedPlayer.playerDetails || cupidoVotedPlayers.playerDetails2 == assassinVotedPlayer.playerDetails
        return cupidoKilled
    }

    private fun veggenteDiscoverKiller(
        playersDetails: List<PlayerDetails>,
        votedPlayerByRole: Map<Role, MostVotedPlayer>
    ): Boolean {
        val validPlayers = playersDetails.toMutableList()
        val assassinVotedPlayer = votedPlayerByRole[Role.ASSASSINO] as MostVotedPlayer.SinglePlayer

        //if faciliCostumi did not saved anyone and the killed player was a veggente, return false
        if (!faciliCostumiSavedPlayer(votedPlayerByRole)) {
            if (killedPlayerIsCupido(votedPlayerByRole)) {
                val cupidoVotedPlayers =
                    votedPlayerByRole[Role.CUPIDO] as MostVotedPlayer.PairPlayers
                validPlayers.remove(cupidoVotedPlayers.playerDetails1)
                validPlayers.remove(cupidoVotedPlayers.playerDetails2)
            } else {
                validPlayers.remove(assassinVotedPlayer.playerDetails)
            }
        }

        if (validPlayers.none { it.role == Role.VEGGENTE && it.alive })
            return false

        val veggenteVotedPlayer = votedPlayerByRole[Role.VEGGENTE] as MostVotedPlayer.SinglePlayer
        return veggenteVotedPlayer.playerDetails.role == Role.ASSASSINO
    }
}