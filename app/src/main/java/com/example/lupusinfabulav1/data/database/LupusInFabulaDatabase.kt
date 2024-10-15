package com.example.lupusinfabulav1.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lupusinfabulav1.data.database.entity.Player
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.data.database.entity.PlayersListConverter

@Database(entities = [Player::class, PlayersList::class], version = 2, exportSchema = false)
@TypeConverters(PlayersListConverter::class) // Register the converter
abstract class LupusInFabulaDatabase() : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun playersListDao(): PlayersListDao

    companion object {
        @Volatile
        private var INSTANCE: LupusInFabulaDatabase? = null

        fun getDatabase(context: Context): LupusInFabulaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context = context, klass = LupusInFabulaDatabase::class.java, name = "lupus_in_fabula_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}