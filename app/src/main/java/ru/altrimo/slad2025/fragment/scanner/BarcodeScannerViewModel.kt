package ru.altrimo.slad2025.fragment.scanner

import android.app.Application
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.altrimo.slad2025.data.Preferences
import ru.altrimo.slad2025.viewmodel.base.SingleLiveEvent
import java.util.concurrent.ExecutionException
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    application: Application,
    private val preferences: Preferences
) : AndroidViewModel(application) {

    val isComputerVision = SingleLiveEvent<Boolean>()

    init {
        isComputerVision.postValue(preferences.isComputerVision)
    }

    fun changeStateComputerVision(isComputerVision: Boolean) {
        if (isComputerVision) {
            preferences.isComputerVision = false
            this.isComputerVision.postValue(false)
        } else {
            preferences.isComputerVision = true
            this.isComputerVision.postValue(true)
        }
    }


    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null

    val processCameraProvider: LiveData<ProcessCameraProvider>
        get() {
            if (cameraProviderLiveData == null) {
                cameraProviderLiveData = MutableLiveData()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication())
                cameraProviderFuture.addListener(
                    {
                        try {
                            cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                        } catch (e: ExecutionException) {
                            // Handle any errors (including cancellation) here.
                            Log.e(TAG, "Unhandled exception", e)
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Unhandled exception", e)
                        }
                    }, ContextCompat.getMainExecutor(getApplication())
                )
            }
            return cameraProviderLiveData!!
        }

    companion object {
        private const val TAG = "CameraXViewModel"
    }
}