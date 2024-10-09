package com.example.lupusinfabulav1.ui.util

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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

// Function to retrieve a Bitmap from a URI using Coil in a synchronous way
fun getBitmapFromUriNonComposable(context: Context, uri: String): Bitmap? {
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(uri)
        .build()

    return try {
        var bitmap: Bitmap? = null
        // Launch a coroutine to load the image asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    bitmap = drawable.bitmap
                }
            }
        }
        bitmap // Return the bitmap (might be null if loading is not finished)
    } catch (e: Exception) {
        null
    }
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

