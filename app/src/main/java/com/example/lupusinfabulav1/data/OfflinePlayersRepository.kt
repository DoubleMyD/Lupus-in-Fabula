package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.data.database.PlayerDao
import com.example.lupusinfabulav1.data.entity.Player
import kotlinx.coroutines.flow.Flow

class OfflinePlayersRepository(private val playerDao: PlayerDao) : PlayersRepository {

    override fun getAllPlayersStream(): Flow<List<Player>> = playerDao.getAllPlayers()

    override fun getPlayerStream(id: Int): Flow<Player?> = playerDao.getPlayer(id)

    override suspend fun insertPlayer(player: Player) = playerDao.insert(player)

    override suspend fun deletePlayer(player: Player) = playerDao.delete(player)

    override suspend fun updatePlayer(player: Player) = playerDao.update(player)
}