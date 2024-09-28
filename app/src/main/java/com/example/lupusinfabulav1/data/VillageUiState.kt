package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.voting.RoleVotes

data class VillageUiState(
    val isGameStarted: Boolean = false,
    val players: List<Player> = PlayersRepository.players,
    val currentRole: Role = Role.CITTADINO,
    val selectedPlayer: Player = players.first(),
    val votedPlayerByRole: Map<Role, Player> = emptyMap(),   //Per ogni ruolo contiene il giocatore votato nell'ultimo turno del ruolo
    val currentVoting: RoleVotes = RoleVotes(currentRole, emptyList(), emptyList(), emptyList()),
    )