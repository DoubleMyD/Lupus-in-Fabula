package com.example.lupusinfabulav1.ui

import com.example.lupusinfabulav1.data.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.MostVotedPlayer
import com.example.lupusinfabulav1.model.RoleVotes

data class VillageUiState(
    val round: Int = 0,
    val gameStarted: Boolean = false,
    val isGameFinished: Boolean = false,
    val winnerPlayerDetails: List<PlayerDetails> = emptyList(),
    val playerDetails: List<PlayerDetails> = FakePlayersRepository.playerDetails,
    val currentRole: Role = Role.CITTADINO,
    val selectedPlayerDetails: PlayerDetails? = null,
    val votedPlayerByRole: Map<Role, MostVotedPlayer> = emptyMap(),   //Per ogni ruolo contiene il giocatore votato nell'ultimo turno del ruolo
    val currentVoting: RoleVotes = RoleVotes(currentRole, emptyList(), emptyList(), emptyList()),
    )