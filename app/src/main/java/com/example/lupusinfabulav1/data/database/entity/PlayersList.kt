package com.example.lupusinfabulav1.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "players_lists")
data class PlayersList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @TypeConverters(PlayersListConverter::class) // Apply TypeConverter
    val playersId: List<Int> = emptyList()
)

// TypeConverter for converting List<Int> to String and vice versa
class PlayersListConverter {

    @TypeConverter
    fun fromStringToPlayersIdList(playersId: String?): List<Int> {
        if (playersId.isNullOrEmpty()) {
            return emptyList()
        }
        return playersId.split(",").mapNotNull { it.toIntOrNull() } // Safely map to Int
    }

    @TypeConverter
    fun fromPlayersIdListToString(playersIdList: List<Int>?): String {
        return playersIdList?.joinToString(",") ?: "" // Handle null or empty list
    }
}

