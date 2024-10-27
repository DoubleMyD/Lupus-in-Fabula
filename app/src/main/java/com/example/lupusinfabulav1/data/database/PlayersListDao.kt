package com.example.lupusinfabulav1.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayersListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playersList: PlayersList)

    @Update
    suspend fun update(playersList: PlayersList)

    @Delete
    suspend fun delete(playersList: PlayersList)

    @Query("SELECT * from players_lists WHERE id = :id")
    fun getPlayersList(id: Int): Flow<PlayersList>

    @Query("SELECT * from players_lists WHERE id = :id")
    suspend fun getPlayersListSync(id: Int): PlayersList // Synchronous retrieval

    @Query("SELECT * from players_lists ORDER BY name ASC")
    fun getAllPlayersLists(): Flow<List<PlayersList>>
}