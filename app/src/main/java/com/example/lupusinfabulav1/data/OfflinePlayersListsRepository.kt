package com.example.lupusinfabulav1.data

import androidx.room.Transaction
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.database.PlayersListDao
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.toPlayerDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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


    override suspend fun getAllListsWithPlayersDetails(): Map<PlayersList, List<PlayerDetails>> {
        val playersLists = playersListDao.getAllPlayersLists().first()
        return playersLists.associateWith { playersList ->
            getPlayersDetailsFromPlayersList(playersList.id)
        }
    }

    override suspend fun getPlayersDetailsFromPlayersList(listId: Int): List<PlayerDetails> {
        val playersList = playersListDao.getPlayersListSync(listId)
        return playersList.playersId.map { playerId ->
            playersRepository.getPlayerStream(playerId).first()?.toPlayerDetails() ?: PlayerDetails(
                id = -1,
                name = "error", imageSource = PlayerImageSource.Resource(
                    R.drawable.ic_launcher_foreground
                )
            )  // Assuming getPlayerById returns the Player entity
        }
    }

//    override suspend fun updatePlayersIdOfList(
//        listId: Int,
//        addedPlayers: List<Int>,
//        removedPlayers: List<Int>
//    ) {
//        val playersList = playersListDao.getPlayersList(listId).first()
//        val existingPlayerIds = playersList.playersId.toSet()
//
//        val validAddedPlayersId = addedPlayers.filterNot { it in existingPlayerIds }.toSet()
//        val validRemovedPlayersId = removedPlayers.filter { it in existingPlayerIds }.toSet()
//
//        val updatedPlayersId = (playersList.playersId + validAddedPlayersId) - validRemovedPlayersId
//
//        val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//        playersListDao.update(updatedPlayersList)
//    }

    @Transaction // Ensure atomic operation
    override suspend fun updatePlayersIdOfList(
        listId: Int,
        addedPlayers: List<Int>,
        removedPlayers: List<Int>
    ) {
        val playersList = playersListDao.getPlayersListSync(listId)
        val updatedPlayerIds = (playersList.playersId + addedPlayers - removedPlayers.toSet()).distinct()

        playersListDao.update(playersList.copy(playersId = updatedPlayerIds))
    }



    override suspend fun addPlayerIdToList(listId: Int, vararg playerIds: Int) {
        updatePlayerIdsInList(listId) { existingIds ->
            (existingIds + playerIds).distinct().map { it as Int }
        }
    }

    override suspend fun removePlayerIdFromList(listId: Int, vararg playerIds: Int) {
        updatePlayerIdsInList(listId) { existingIds ->
            existingIds - playerIds.toSet()
        }
    }

    /**
     * Helper function to update player IDs in a transactional manner, avoiding duplicates or inconsistencies.
     */
    private suspend fun updatePlayerIdsInList(listId: Int, updateLogic: (List<Int>) -> List<Int>) {
        val playersList = playersListDao.getPlayersListSync(listId)
        val updatedPlayersId = updateLogic(playersList.playersId)

        playersListDao.update(playersList.copy(playersId = updatedPlayersId))
    }

//    // Refactor to handle both single and multiple player IDs
//    override suspend fun addPlayerIdToList(listId: Int, vararg playerIds: Int) {
//        // Get the current PlayersList from the database
//        val playersList = playersListDao.getPlayersList(listId).first()
//
//        // Create a set of existing player IDs to avoid duplicates
//        val existingPlayerIds = playersList.playersId.toSet()
//
//        // Filter new player IDs to exclude duplicates
//        val newPlayerIds = playerIds.filterNot { it in existingPlayerIds }
//
//        if (newPlayerIds.isNotEmpty()) {
//            // Add new player IDs to the existing list
//            val updatedPlayersId = playersList.playersId + newPlayerIds
//
//            // Create and update the new PlayersList
//            val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//            playersListDao.update(updatedPlayersList)
//        }
//    }
//
//    override suspend fun removePlayerIdFromList(listId: Int, vararg playerIds: Int) {
//        // Get the current PlayersList from the database
//        val playersList = playersListDao.getPlayersList(listId).first()
//
//        // Create a set of existing player IDs
//        val existingPlayerIds = playersList.playersId.toSet()
//
//        // Filter out the IDs that are not present in the existing list
//        val idsToRemove = playerIds.filter { it in existingPlayerIds }
//
//        if (idsToRemove.isNotEmpty()) {
//            // Remove the player IDs from the existing list
//            val updatedPlayersId = playersList.playersId - idsToRemove.toSet()
//
//            // Create and update the new PlayersList
//            val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//            playersListDao.update(updatedPlayersList)
//        }
//    }
}


//    override suspend fun addPlayerIdToList(listId: Int, playersId: List<Int>) {
//        // Get the current PlayersList from the database
//        val playersList = playersListDao.getPlayersList(listId).first()
//
//        // Create a set of existing player IDs to avoid duplicates
//        val existingPlayerIds = playersList.playersId.toSet()
//
//        // Filter the new player IDs to keep only those not already in the list
//        val newPlayerIds = playersId.filterNot { it in existingPlayerIds }
//
//        // Add the new player IDs to the existing list
//        val updatedPlayersId = playersList.playersId + newPlayerIds
//
//        // Create an updated PlayersList and update it in the database
//        val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//        playersListDao.update(updatedPlayersList)
//    }
//
//    override suspend fun removePlayerIdFromList(listId: Int, playersId: List<Int>) {
//        // Get the current PlayersList from the database
//        val playersList = playersListDao.getPlayersList(listId).first()
//
//        // Create a set of existing player IDs to avoid duplicates
//        val existingPlayerIds = playersList.playersId.toSet()
//
//        // Filter the new player IDs to keep only those not already in the list
//        val newPlayerIds = playersId.filter { it in existingPlayerIds }
//
//        // Add the new player IDs to the existing list
//        val updatedPlayersId = playersList.playersId - newPlayerIds.toSet()
//
//        // Create an updated PlayersList and update it in the database
//        val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//        playersListDao.update(updatedPlayersList)
//    }
//
//
//    override suspend fun addPlayerIdToList(listId: Int, playerId: Int) {
//        // Get the current PlayersList from the database
//        val playersList = playersListDao.getPlayersList(listId)
//            .first() // Retrieve the list (use Flow/collect in real-time scenarios)
//
//        // Add the new playerId to the list if it doesn't already exist
//        if (!playersList.playersId.contains(playerId)) {
//            val updatedPlayersId = playersList.playersId.toMutableList().apply { add(playerId) }
//
//            // Create an updated PlayersList and update it in the database
//            val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//            playersListDao.update(updatedPlayersList)
//        }
//    }
//
//    override suspend fun removePlayerIdFromList(listId: Int, playerId: Int) {
//        // Get the current PlayersList from the database
//        val playersList = playersListDao.getPlayersList(listId).first() // Retrieve the list
//
//        // Remove the playerId if it exists in the list
//        if (playersList.playersId.contains(playerId)) {
//            val updatedPlayersId = playersList.playersId.toMutableList().apply { remove(playerId) }
//
//            // Create an updated PlayersList and update it in the database
//            val updatedPlayersList = playersList.copy(playersId = updatedPlayersId)
//            playersListDao.update(updatedPlayersList)
//        }
//    }
