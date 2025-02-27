package ru.altrimo.slad2025.fragment.scanner.processor

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage

abstract class VisionProcessorBase<T>(context: Context) : VisionImageProcessor {

    private var activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    private var isShutdown = false

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(image: ImageProxy) {
        if (isShutdown) {
            return
        }
        val originalCameraImage =  image.toBitmap()
        val mediaImage = image.image ?: return
        val height = mediaImage.height
        val width = mediaImage.width
        val c1x = (width * 0.125).toInt() + 150
        val c1y = (height * 0.25).toInt() - 25
        val c2x = (width * 0.875).toInt() - 150
        val c2y = (height * 0.75).toInt() + 25
        val rect = Rect(c1x, c1y, c2x, c2y)
        val crop = Bitmap.createBitmap(
            originalCameraImage,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )
        val rImage: Bitmap = crop.rotate(90F)
        requestDetectInImage(
            InputImage.fromBitmap(rImage, image.imageInfo.rotationDegrees)
        ).addOnCompleteListener { image.close() }
    }

    private fun requestDetectInImage(
        image: InputImage
    ): Task<T> {
        return setUpListener(
            detectInImage(image)
        )
    }

    private fun setUpListener(task: Task<T>): Task<T> {
        return task
            .addOnSuccessListener(executor) { results: T ->
                this@VisionProcessorBase.onSuccess(results)
            }
            .addOnFailureListener(executor) { e: Exception ->
                e.printStackTrace()
                this@VisionProcessorBase.onFailure(e)
            }
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>
    protected abstract fun onSuccess(results: T)
    protected abstract fun onFailure(e: Exception)

}