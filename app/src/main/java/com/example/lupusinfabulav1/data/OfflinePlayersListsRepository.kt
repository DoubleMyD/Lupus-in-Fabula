package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.database.PlayersListDao
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class OfflinePlayersListsRepository(
    private val playersListDao: PlayersListDao,
    private val playersRepository: PlayersRepository
) : PlayersListsRepository {

    override fun getAllPlayersListsStream(): Flow<List<PlayersList>> =
        playersListDao.getAllPlayersLists()

    override fun getPlayersListStream(id: Int): Flow<PlayersList?> =
        playersListDao.getPlayersList(id)

    override suspend fun insertPlayersList(playersList: PlayersList) =
        playersListDao.insert(playersList)

    override suspend fun deletePlayersList(playersList: PlayersList) =
        playersListDao.delete(playersList)

    override suspend fun updatePlayersList(playersList: PlayersList) =
        playersListDao.update(playersList)

    override suspend fun getPlayersDetailsFromPlayersList(listId: Int): List<PlayerDetails> {
        val playersList = playersListDao.getPlayersList(listId)
            .first() // getPlayersList returns a Flow, so you need to collect it.
        return playersList.playersId.map { playerId ->
            playersRepository.getPlayerStream(playerId).first()?.toPlayerDetails() ?: PlayerDetails(
                "error", imageSource = PlayerImageSource.Resource(
                    R.drawable.ic_launcher_foreground
                )
            )  // Assuming getPlayerById returns the Player entity
        }
    }

    override suspend fun getPlayersDetailsFromList(listId: Int): Flow<List<PlayerDetails>> {
        return playersListDao.getPlayersList(listId)
            .map { playersList ->
                playersList.playersId.map { playerId ->
                    playersRepository.getPlayerStream(playerId).firstOrNull()?.toPlayerDetails()
                        ?: PlayerDetails(
                            "error", imageSource = PlayerImageSource.Resource(
                                R.drawable.ic_launcher_foreground
                            )
                        )
                }
            }
    }
}