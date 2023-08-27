package com.shino72.saying.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.shino72.saying.utils.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class ImageSaveRepository @Inject constructor(private val context : Context ){
    fun saveImageToStorage(bitmap: Bitmap) : Flow<Status<String>> = flow{
        emit(Status.Loading())
        try {
            val filename = "${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                    // 미디어 스캐닝 실행
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(imageUri.toString()),
                        null
                    ) { _, _ -> }
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)

                // 미디어 스캐닝 실행
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(image.absolutePath),
                    null
                ) { _, _ -> }
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                emit(Status.Success(null))
            }
            fos?.close()
        }
        catch (e: Exception) {
            emit(Status.Error(message = e.localizedMessage ?: ""))
        }
    }

}