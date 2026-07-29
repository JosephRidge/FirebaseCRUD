package com.jayr.firecrud.data.repository.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.rpc.ErrorInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.IOException
import kotlin.coroutines.resumeWithException

/**
 * Uploads raw image bytes to Cloudinary using an UNSIGNED upload preset.
 * Never embed your Cloudinary API secret in the app — the unsigned preset
 * is configured in the Cloudinary console with restrictions (folder, size,
 * format) so the app can upload without holding credentials that could be
 * extracted from the APK.
 *
 * Source Documentation for reference:https://cloudinary.com/documentation/android_image_and_video_upload
 * Setup: https://cloudinary.com/documentation/android_quickstart
 */
class CloudinaryImageUpload(private val context: Context) {

    suspend fun uploadImages(localImagePaths: List<String>): List<String> = coroutineScope {
        localImagePaths.map { path ->
            async { uploadSingleImage(readBytesFromPath(path)) }
        }.awaitAll()
    }

    private fun readBytesFromPath(path: String): ByteArray {
        val uri = Uri.parse(path)
        return if (uri.scheme == "content" || uri.scheme == "file") {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("Could not open input stream for $path")
        } else {
            File(path).readBytes()
        }
    }

    private suspend fun uploadSingleImage(bytes: ByteArray): String =
        suspendCancellableCoroutine { cont ->
            MediaManager.get().upload(bytes)
                .unsigned("your_unsigned_preset_name")
                .option("folder", "task_manager_uploads")
                .callback(object : UploadCallback {
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as String
                        cont.resume(url) {}
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        cont.resumeWithException(Exception(error.description))
                    }
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytesUploaded: Long, totalBytes: Long) {}
                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
}