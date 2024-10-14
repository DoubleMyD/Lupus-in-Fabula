package com.example.lupusinfabulav1.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.lupusinfabulav1.data.ImageRepository
import com.example.lupusinfabulav1.data.database.ImageIO
import com.example.lupusinfabulav1.data.database.entity.Player
import com.example.lupusinfabulav1.ui.util.getBitmapFromUriNonComposable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

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

// This extension function will retrieve a Bitmap based on the source type.
fun PlayerImageSource.getBitmap(context: Context): Bitmap? {
    return when (this) {
        is PlayerImageSource.Resource -> getBitmapFromDrawable(context, resId)
        is PlayerImageSource.UriSource -> getBitmapFromUriNonComposable(context, uri)
    }
}

data class PlayerDetails(
    val name: String,
    val role: Role = Role.CITTADINO,
    val alive: Boolean = true,
    val imageSource: PlayerImageSource = PlayerImageSource.Resource(ImageRepository.defaultImages.random())
) {
    // Provide methods that return a new instance instead of mutating the object
    fun kill(): PlayerDetails = copy(alive = false)
    fun revive(): PlayerDetails = copy(alive = true)
    fun changeRole(newRole: Role): PlayerDetails = copy(role = newRole)
}

fun PlayerDetails.toPlayer(context: Context): Player = Player(
    name = name,
    role = role,
    alive = alive,
    imageSource = {
        val bitmap = imageSource.getBitmap(context)
        // Use runBlocking to execute the coroutine andget the result
        val imageSourceLocation = runBlocking {
            ImageIO.getImageIO(context).saveImageToStorage(context, bitmap!!, name)
        }
        imageSourceLocation // Return the imageSourceLocation
    }.toString()
)

fun Player.toPlayerDetails(): PlayerDetails = PlayerDetails(
    name = name,
    role = role,
    alive = alive,
    imageSource = PlayerImageSource.UriSource(imageSource)
)

@Composable
fun getBitmapFromUri(uri: String): Bitmap? {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        val result = (imageLoader.execute(request) as SuccessResult).drawable
        bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
    }

    return bitmap
}

// on below line we are creating a function to get bitmap
// from image and passing params as context and an int for drawable.
fun getBitmapFromDrawable(context: Context, drawable: Int): Bitmap {

    // on below line we are getting drawable
    val db = ContextCompat.getDrawable(context, drawable)

    // in below line we are creating our bitmap and initializing it.
    val bit = Bitmap.createBitmap(
        db!!.intrinsicWidth, db.intrinsicHeight, Bitmap.Config.ARGB_8888
    )

    // on below line we are
    // creating a variable for canvas.
    val canvas = Canvas(bit)

    // on below line we are setting bounds for our bitmap.
    db.setBounds(0, 0, canvas.width, canvas.height)

    // on below line we are simply
    // calling draw to draw our canvas.
    db.draw(canvas)

    // on below line we are
    // returning our bitmap.
    return bit
}

