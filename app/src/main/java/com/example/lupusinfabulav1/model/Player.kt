package com.example.lupusinfabulav1.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.lupusinfabulav1.data.ImageRepository

sealed class PlayerImageSource {
    data class Resource(@DrawableRes val resId: Int) : PlayerImageSource()
    data class UriSource(val uri: String) : PlayerImageSource() // Use String for simplicity
}

@Composable
fun PlayerImageSource.getPainter(): Painter {
    return when (this) {
        is PlayerImageSource.Resource -> painterResource(id = this.resId)
        is PlayerImageSource.UriSource -> rememberAsyncImagePainter(model = this.uri)
    }
}

data class Player(
    val name: String,
    val role: Role = Role.CITTADINO,
    val alive: Boolean = true,
    val imageSource: PlayerImageSource = PlayerImageSource.Resource(ImageRepository.defaultImages.random())
) {
    // Provide methods that return a new instance instead of mutating the object
    fun kill(): Player = copy(alive = false)
    fun revive(): Player = copy(alive = true)
    fun changeRole(newRole: Role): Player = copy(role = newRole)
}