package com.example.lupusinfabulav1.model

import androidx.annotation.DrawableRes
import com.example.lupusinfabulav1.R

data class Player(
    val name: String,
    val role: Role = Role.CITTADINO,
    val alive: Boolean = true,
    @DrawableRes val imageRes: Int = drawableResources.random(),
) {
    // Provide methods that return a new instance instead of mutating the object
    fun kill(): Player = copy(alive = false)
    fun revive(): Player = copy(alive = true)
    fun changeRole(newRole: Role): Player = copy(role = newRole)

}

private val drawableResources = arrayOf(
    R.drawable.android_superhero1,
    R.drawable.android_superhero2,
    R.drawable.android_superhero3,
    R.drawable.android_superhero4,
    R.drawable.android_superhero5,
    R.drawable.android_superhero6
)