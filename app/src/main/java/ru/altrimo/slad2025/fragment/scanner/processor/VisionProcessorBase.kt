package ru.altrimo.slad2025.fragment.scanner.processor

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage

abstract class VisionProcessorBase<T> : VisionImageProcessor {

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    private var isShutdown = false

    @SuppressLint("UnsafeOptInUsageError")
    override fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay) {
        if (isShutdown) {
            return
        }
        requestDetectInImage(
            InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees),
            graphicOverlay
        ).addOnCompleteListener { image.close() }
    }

    private fun requestDetectInImage(
        image: InputImage,
        graphicOverlay: GraphicOverlay
    ): Task<T> {
        return setUpListener(
            detectInImage(image),
            graphicOverlay
        )
    }

    private fun setUpListener(task: Task<T>, graphicOverlay: GraphicOverlay): Task<T> {
        return task
            .addOnSuccessListener(executor) { results: T ->
                graphicOverlay.clear()
                this@VisionProcessorBase.onSuccess(results, graphicOverlay)
            }
            .addOnFailureListener(executor) { e: Exception ->
                e.printStackTrace()
                graphicOverlay.clear()
                graphicOverlay.postInvalidate()
                this@VisionProcessorBase.onFailure(e)
            }
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>
    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)
    protected abstract fun onFailure(e: Exception)

}