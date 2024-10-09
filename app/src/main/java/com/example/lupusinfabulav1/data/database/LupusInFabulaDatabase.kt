package com.example.lupusinfabulav1.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lupusinfabulav1.data.entity.Player

@Database(entities = [Player::class], version = 1, exportSchema = false)
abstract class LupusInFabulaDatabase() : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

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