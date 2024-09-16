package com.example.lupusinfabulav1.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.lupusinfabulav1.R

enum class Role (val roleName: String, @DrawableRes val icon: Int, val color: Color){
    CITTADINO("Cittadino", R.drawable.ic_launcher_foreground, Color.Green.copy(alpha = 0.5f)),
    ASSASSINO("Assassino", R.drawable.wolf_muso, Color.Blue.copy(alpha = 0.5f)),
    VEGGENTE("Veggie", R.drawable.veggente_luna, Color.DarkGray.copy(alpha = 0.5f)),
    FACILI_COSTUMI("Facile Costumi", R.drawable.facilicostumi_butterfly, Color.Magenta.copy(alpha = 0.5f)),
    MEDIUM("Medium", R.drawable.baseline_sunny_24, Color.Yellow.copy(alpha = 0.5f)),// Assign string value here
    CUPIDO("Cupido", R.drawable.cupido_bow, Color.Red.copy(alpha = 0.5f))  // Assig
}

sealed class RoleVotedPlayers {
    data class SinglePlayer(val player: Player?) : RoleVotedPlayers()
    data class MultiplePlayers(val players: List<Player>) : RoleVotedPlayers()
}