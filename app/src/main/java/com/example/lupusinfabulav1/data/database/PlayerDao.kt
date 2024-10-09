package com.example.lupusinfabulav1.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lupusinfabulav1.data.entity.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(player: Player)

    @Update
    suspend fun update(player: Player)

    @Delete
    suspend fun delete(player: Player)

    @Query("SELECT * from players WHERE id = :id")
    fun getPlayer(id: Int): Flow<Player>

    @Query("SELECT * from players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<Player>>

}