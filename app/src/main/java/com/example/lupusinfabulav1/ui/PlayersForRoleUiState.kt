package com.example.lupusinfabulav1.ui

import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Role
import kotlin.math.abs

data class PlayersForRoleUiState(
    val playersSize: Int = PlayerManager.players.value.size,
    val startRange: Int = 1,
    val finishRange: Int = 10,
    val minAllowedValue: Int = 1,
    val maxAllowedValue: Int = abs(playersSize - Role.entries.size + 1),
    val sliderValue: Float = 1f,
    val currentRole: Role = Role.CITTADINO,
    val playersForRole: Map<Role, Int> = Role.entries.associateWith { 1 },
    val remainingPlayers: Int = abs(playersSize - playersForRole.values.sum()),
    )
