package com.jayr.firecrud.data.repository.cloudinary

import android.content.Context
import com.google.rpc.ErrorInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * Uploads raw image bytes to Cloudinary using an UNSIGNED upload preset.
 * Never embed your Cloudinary API secret in the app — the unsigned preset
 * is configured in the Cloudinary console with restrictions (folder, size,
 * format) so the app can upload without holding credentials that could be
 * extracted from the APK.
 *
 * Source Documentation for reference:https://cloudinary.com/documentation/android_image_and_video_upload
 */
class CloudinaryImageUpload (private val context: Context) {

    suspend fun uploadImage(bytes: ByteArray): String = suspendCancellableCoroutine { cont ->
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