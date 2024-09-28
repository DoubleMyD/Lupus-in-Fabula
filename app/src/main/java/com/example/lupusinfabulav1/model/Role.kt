package com.example.lupusinfabulav1.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.lupusinfabulav1.R

private const val colorAlpha = 0.5f

enum class Role (val roleName: String, @DrawableRes val image: Int, val color: Color){
    CITTADINO("Cittadino", R.drawable.ic_launcher_foreground, Color.Green.copy(alpha = colorAlpha)),
    ASSASSINO("Assassino", R.drawable.wolf_muso, Color.Blue.copy(alpha = colorAlpha)),
    VEGGENTE("Veggie", R.drawable.veggente_luna, Color.DarkGray.copy(alpha = colorAlpha)),
    FACILI_COSTUMI("Facile Costumi", R.drawable.facilicostumi_butterfly, Color.Magenta.copy(alpha = colorAlpha)),
    MEDIUM("Medium", R.drawable.baseline_sunny_24, Color.Yellow.copy(alpha = colorAlpha)),// Assign string value here
    CUPIDO("Cupido", R.drawable.cupido_bow, Color.Red.copy(alpha = colorAlpha))  // Assig
}