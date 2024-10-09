package com.example.lupusinfabulav1.data.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Room
import com.example.lupusinfabulav1.data.database.LupusInFabulaDatabase.Companion
import java.io.File

class ImageIO {

    suspend fun saveImageToStorage(context: Context, bitmap: Bitmap, playerName: String): String {
        // Define the file where the image will be stored
        val file = File(context.filesDir, "$playerName.jpg")

        // Save the bitmap to the file
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        // Return the file path as a string to store in Room
        return file.absolutePath
    }

    fun getBitmapFromFilePath(filePath: String): Bitmap? {
        val file = File(filePath)
        return if (file.exists()) {
            BitmapFactory.decodeFile(filePath)
        } else {
            null // Return null if the file doesn't exist
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ImageIO? = null

        fun getImageIO(context: Context): ImageIO {
            return INSTANCE ?: synchronized(this) {
                ImageIO()
                    .also { INSTANCE = it }
            }
        }
    }
}