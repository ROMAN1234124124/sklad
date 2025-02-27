package ru.altrimo.slad2025.fragment.scanner

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Size
import android.view.MotionEvent
import android.view.View
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.databinding.FragmentBarcodeScannerBinding
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.fragment.scanner.processor.BarcodeScannerProcessor
import ru.altrimo.slad2025.fragment.scanner.processor.VisionImageProcessor
import java.util.ArrayList

@AndroidEntryPoint
class BarcodeScannerFragment : ViewBindingFragment<FragmentBarcodeScannerBinding>(),
    BarcodeScannerProcessor.BarcodeScannerListener {

    override val inflaterDelegate by inflaterDelegate()
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewView: PreviewView? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var camera: Camera? = null

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val defaultResolution: Size = Size(1080, 1920)


    override fun onInflationComplete() {
        previewView = binding.previewView
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[BarcodeScannerViewModel::class.java].processCameraProvider.observe(this) { cameraProvider ->
            this.cameraProvider = cameraProvider
            bindAllCameraUseCases()
        }
    }

    override fun onBarcodes(results: List<String>) {
        if (results.isNotEmpty()) {
            beep()
            val result = Bundle().apply {
                putStringArrayList(SCAN_RESULT, ArrayList(results))
            }
            parentFragmentManager.setFragmentResult(SCAN_REQUEST, result)
        }
    }


    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
            bindActions()
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider?.unbind(previewUseCase)
        }
        cameraProvider?.unbind(previewUseCase)
        val builder = Preview.Builder()
        @Suppress("DEPRECATION")
        builder.setTargetResolution(defaultResolution)
        previewUseCase = builder.build()
        previewUseCase?.surfaceProvider = previewView?.surfaceProvider
        camera = cameraProvider?.bindToLifecycle(
            this,
            cameraSelector,
            previewUseCase
        )
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor = BarcodeScannerProcessor(requireActivity().applicationContext, this)
        val builder = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        @Suppress("DEPRECATION")
        builder.setTargetResolution(defaultResolution)
        analysisUseCase = builder.build()
        analysisUseCase?.setAnalyzer(ContextCompat.getMainExecutor(requireActivity().applicationContext)) {
            try {
                imageProcessor?.processImageProxy(it)
            } catch (e: MlKitException) {
                e.printStackTrace()
            }
        }
        cameraProvider?.bindToLifecycle(
            this,
            cameraSelector,
            analysisUseCase
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bindActions() {
        binding.previewView.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                MotionEvent.ACTION_UP -> {
                    val factory = binding.previewView.meteringPointFactory
                    val point = factory.createPoint(motionEvent.x, motionEvent.y)
                    val action = FocusMeteringAction.Builder(point).build()
                    camera?.cameraControl?.startFocusAndMetering(action)
                    return@setOnTouchListener true
                }

                else -> return@setOnTouchListener false
            }
        }
    }


    private fun beep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
    }


    override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        imageProcessor?.run { this.stop() }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run { this.stop() }
    }


    companion object {
        const val SCAN_RESULT = "scan_result"
        const val SCAN_REQUEST = "scan_request"
    }

}
