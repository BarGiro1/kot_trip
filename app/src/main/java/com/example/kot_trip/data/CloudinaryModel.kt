package com.example.kot_trip.data

import android.content.Context
import android.graphics.Bitmap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.cloudinary.android.policy.UploadPolicy
import com.example.kot_trip.base.App
import java.io.File
import java.io.FileOutputStream

class CloudinaryModel {

    companion object {
        private var isInitialized = false
    }

    init {
        // Initialize only once
        if (!isInitialized) {
            val config = mapOf(
                "cloud_name" to "dbargqizf",
                "api_key" to "633986574927673",
                "api_secret" to "-k_83ez2E7Kx-7LJPmkxV6FfrHE"
            )

            App.Globals.context?.let {
                MediaManager.init(it, config)
                MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.Builder()
                    .maxConcurrentRequests(3)
                    .networkPolicy(UploadPolicy.NetworkType.UNMETERED)
                    .build()
            }
            isInitialized = true
        }
    }

    fun uploadBitmap(bitmap: Bitmap, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val context = App.Globals.context ?: return
        val file = bitmapToFile(bitmap, context)

        MediaManager.get().upload(file.path)
            .option(
                "folder",
                "images"
            ) // Optional: Specify a folder in your Cloudinary account
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    // Called when upload starts
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Called during upload progress
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val publicUrl = resultData["secure_url"] as? String ?: ""
                    onSuccess(publicUrl) // Return the URL of the uploaded image
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    TODO("Not yet implemented")
                }

            })
            .dispatch()
    }

    fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return file
    }
}