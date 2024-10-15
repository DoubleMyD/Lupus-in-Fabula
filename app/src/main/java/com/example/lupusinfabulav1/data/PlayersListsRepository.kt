package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.data.database.entity.Player
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import kotlinx.coroutines.flow.Flow

interface PlayersListsRepository {

    fun getAllPlayersListsStream(): Flow<List<PlayersList>>

    fun getPlayersListStream(id: Int): Flow<PlayersList?>

    suspend fun insertPlayersList(playersList: PlayersList)

    suspend fun deletePlayersList(playersList: PlayersList)

    suspend fun updatePlayersList(playersList: PlayersList)



    suspend fun getPlayersDetailsFromPlayersList(listId: Int): List<PlayerDetails>

    suspend fun getPlayersDetailsFromList(listId: Int): Flow<List<PlayerDetails>>

}