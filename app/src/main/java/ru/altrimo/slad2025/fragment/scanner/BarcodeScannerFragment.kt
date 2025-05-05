package ru.altrimo.slad2025.fragment.scanner

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.databinding.FragmentBarcodeScannerBinding
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.fragment.scanner.processor.BarcodeScannerProcessor
import ru.altrimo.slad2025.fragment.scanner.processor.VisionImageProcessor
import java.util.concurrent.atomic.AtomicBoolean

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
    private val viewModel: BarcodeScannerViewModel by viewModels()
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val defaultResolution: Size = Size(1080, 1920)
    private var isHandScanMode: Boolean? = null
    private val isKeyDown = AtomicBoolean(false)


    override fun onInflationComplete() {
        previewView = binding.previewView
        isHandScanMode = arguments?.getBoolean(IS_HAND_SCAN_MODE)
        viewModel.processCameraProvider.observe(this) { cameraProvider ->
            this.cameraProvider = cameraProvider
            bindAllCameraUseCases()
            camera?.let { initializeFlashButton(it) }
            binding.seekbarZoom.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    camera?.cameraControl?.setLinearZoom(progress / 100.toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        binding.imgQrBox.isVisible = isHandScanMode == false

        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    binding.imgQrBox.isVisible = true
                    if (event.repeatCount == 0) {
                        isKeyDown.set(true)
                    }
                } else {
                    isKeyDown.set(false)
                    binding.imgQrBox.isVisible = false
                }
            }
            true
        }
    }

    override fun onBarcodes(results: List<String>) {
        if (results.isNotEmpty()) {
            if (isHandScanMode == true) {
                if (isKeyDown.get()) {
                    beep()
                    val result = Bundle().apply {
                        putStringArrayList(SCAN_RESULT, ArrayList(results))
                    }
                    parentFragmentManager.setFragmentResult(SCAN_REQUEST, result)
                    isKeyDown.set(false)
                }
            } else {
                beep()
                val result = Bundle().apply {
                    putStringArrayList(SCAN_RESULT, ArrayList(results))
                }
                parentFragmentManager.setFragmentResult(SCAN_REQUEST, result)
            }
        }
    }


    private fun initializeFlashButton(cam: Camera) = with(binding) {
        if (cam.cameraInfo.hasFlashUnit()) {
            actionFlashlight.setOnClickListener {
                cam.cameraControl.enableTorch(
                    cam.cameraInfo.torchState.value == TorchState.OFF
                )
            }
            actionFlashlight.isVisible = true
        } else {
            actionFlashlight.isVisible = false
        }

        cam.cameraInfo.torchState.observe(viewLifecycleOwner) { torchState ->
            if (torchState == TorchState.OFF) {
                actionFlashlight.setImageResource(R.drawable.ic_flash_mode_off)
            } else {
                actionFlashlight.setImageResource(R.drawable.ic_flash_mode_on)
            }
        }
    }


    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider?.unbindAll()
            bindPreviewUseCase()
            bindActions()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null || previewView == null) return
        binding.loadingContainer.isVisible = true
        previewUseCase?.let { cameraProvider?.unbind(it) }
        val builder = Preview.Builder()
        @Suppress("DEPRECATION")
        builder.setTargetResolution(defaultResolution)
        previewUseCase = builder.build()
        previewUseCase?.surfaceProvider = previewView?.surfaceProvider
        try {
            camera = cameraProvider?.bindToLifecycle(
                this,
                cameraSelector,
                previewUseCase
            )
            binding.loadingContainer.isVisible = false

        } catch (e: Exception) {
            e.printStackTrace()
            binding.loadingContainer.isVisible = false
        }
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
        const val IS_HAND_SCAN_MODE = "isHandScanMode"

        fun newInstance(isHandScanMode: Boolean): BarcodeScannerFragment {
            val fragment = BarcodeScannerFragment()
            val args = Bundle().apply {
                putBoolean(IS_HAND_SCAN_MODE, isHandScanMode)
            }
            fragment.arguments = args
            return fragment
        }

    }

}
