package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role

data class VillageUiState(
    val isGameStarted: Boolean = false,
    val players: List<Player> = PlayersRepository.players,
    val currentRole: Role = Role.CITTADINO,
    val selectedPlayer: Player = players.first(),

    )