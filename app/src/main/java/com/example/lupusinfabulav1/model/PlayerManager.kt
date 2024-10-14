package com.example.lupusinfabulav1.model

import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerManager() {
    private val _players = MutableStateFlow(FakePlayersRepository.playerDetails)
    val players: StateFlow<List<PlayerDetails>> = _players.asStateFlow()

    fun addPlayer(playerDetails: PlayerDetails) {
        _players.value += playerDetails
    }

    fun removePlayer(playerDetails: PlayerDetails) {
        _players.value -= playerDetails
    }
}
