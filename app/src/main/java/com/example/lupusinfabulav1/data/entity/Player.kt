package com.example.lupusinfabulav1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.Role

@Entity(tableName = "players")
class Player (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val role: Role,
    val alive: Boolean,
    val imageSource: String
)