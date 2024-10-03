package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.model.Player

interface PlayersRepository {
    suspend fun getPlayers(): List<Player>
}


class DatabasePlayerRepository : PlayersRepository {

    // Simulate fetching data from a database
    override suspend fun getPlayers(): List<Player> = FakePlayersRepository.players

}

/*
//replace type string with typeApiService
class NetworkPlayerRepository (private val apiService: String) : PlayersRepository {
    override suspend fun getPlayers(): List<Player> {
        // Simulate fetching data from a network
        return FakePlayersRepository.players
    }
}
*/