package com.example.lupusinfabulav1.model

import androidx.annotation.DrawableRes
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.ImageRepository

data class Player(
    val name: String,
    val role: Role = Role.CITTADINO,
    val alive: Boolean = true,
    @DrawableRes val imageRes: Int = ImageRepository.defaultImages.random(),
) {
    // Provide methods that return a new instance instead of mutating the object
    fun kill(): Player = copy(alive = false)
    fun revive(): Player = copy(alive = true)
    fun changeRole(newRole: Role): Player = copy(role = newRole)
}