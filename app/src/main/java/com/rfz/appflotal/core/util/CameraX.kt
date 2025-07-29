package com.rfz.appflotal.core.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

class CameraX(
    private var context: Context,
    private var owner: LifecycleOwner,
) {
    private var imageCapture: ImageCapture? = null

    fun startCameraPreviewView(): PreviewView {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val previewView = PreviewView(context)
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val camSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        try {
            cameraProviderFuture.get().bindToLifecycle(
                owner,
                camSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return previewView
    }

    fun capturePhoto() =owner.lifecycleScope.launch{
        val imageCapture = imageCapture ?: return@launch

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object :
            ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
            override fun onCaptureStarted() {

            }

            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                owner.lifecycleScope.launch {
                    saveMediaToStorage(
                        imageProxyToBitmap(image),
                        System.currentTimeMillis().toString()
                    )
                }
            }



            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                com.rfz.appflotal.core.util.Commons.showLog("onCaptureSuccess: Uri  ${outputFileResults.savedUri}")

            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                com.rfz.appflotal.core.util.Commons.showLog("onCaptureSuccess: onError")
            }

            override fun onCaptureProcessProgressed(progress: Int) {

            }

            override fun onPostviewBitmapAvailable(bitmap: Bitmap) {

            }


        })


    }

    private suspend fun imageProxyToBitmap(image: ImageProxy): Bitmap =
        withContext(owner.lifecycleScope.coroutineContext) {
            val planeProxy = image.planes[0]
            val buffer: ByteBuffer = planeProxy.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    private suspend fun saveMediaToStorage(bitmap: Bitmap, name: String) {
        withContext(IO) {
            val filename = "$name.jpg"
            var fos: OutputStream? = null
            val tessFolderPath = Environment.DIRECTORY_DOWNLOADS + "/" + "GasMonSoft/"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->

                    val contentValues = ContentValues().apply {

                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,

                            tessFolderPath
                        )
                    }

                    val uridoc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        sdk29AndUp {
                            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        } ?: MediaStore.Downloads.EXTERNAL_CONTENT_URI
                    } else {
                        TODO("VERSION.SDK_INT < Q")
                    }
                    val imageUri: Uri? =
                        resolver.insert( uridoc, contentValues)

                    fos = imageUri?.let { with(resolver) { openOutputStream(it) } }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val image = File(imagesDir, filename).also { fos = FileOutputStream(it) }
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = Uri.fromFile(image)
                    context.sendBroadcast(mediaScanIntent)
                }
            }

            fos?.use {
                val success = async(IO) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                if (success.await()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

}