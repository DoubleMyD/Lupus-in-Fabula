package com.example.lupusinfabulav1.model

class PlayerManager {
    private val _players = mutableListOf<Player>()
    val players: List<Player> get() = _players

    fun addPlayer(player: Player): Boolean{
       return _players.add(player)
    }

    fun removePlayer(player: Player) {
        _players.remove(player)
    }


}