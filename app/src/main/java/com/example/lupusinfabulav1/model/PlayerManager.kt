package com.example.lupusinfabulav1.model

import com.example.lupusinfabulav1.data.FakePlayersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlayerManager {
    private val _players = MutableStateFlow(FakePlayersRepository.players)
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    fun addPlayer(player: Player) {
        _players.value += player
    }

    fun removePlayer(player: Player) {
        _players.value -= player
    }
}
