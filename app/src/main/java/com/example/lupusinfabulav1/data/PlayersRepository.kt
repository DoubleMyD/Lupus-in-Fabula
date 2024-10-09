package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.data.entity.Player
import kotlinx.coroutines.flow.Flow

interface PlayersRepository {
    fun getAllPlayersStream(): Flow<List<Player>>

    fun getPlayerStream(id: Int): Flow<Player?>

    suspend fun insertPlayer(player: Player)

    suspend fun deletePlayer(player: Player)

    suspend fun updatePlayer(player: Player)
}


//class DatabasePlayerRepository : PlayersRepository {
//
//    // Simulate fetching data from a database
//    override suspend fun getAllPlayersStream(): List<Player> = FakePlayersRepository.players
//
//}

/*
//replace type string with typeApiService
class NetworkPlayerRepository (private val apiService: String) : PlayersRepository {
    override suspend fun getPlayers(): List<Player> {
        // Simulate fetching data from a network
        return FakePlayersRepository.players
    }
}
*/