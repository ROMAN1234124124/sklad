package ru.altrimo.slad2025.fragment.scanner.processor

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*

class BarcodeScannerProcessor(
    context: Context,
    private val barcodeScannerListener: BarcodeScannerListener
) : VisionProcessorBase<List<Barcode>>(context) {

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    private var job: Deferred<Unit>
    private val processSet = mutableSetOf<String>()


    private fun CoroutineScope.launchPeriodicAsync(
        repeatMillis: Long,
        action: () -> Unit
    ) = this.async {
        if (repeatMillis > 0) {
            while (isActive) {
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }

    /**
     * Возвращает очень много результатов меньше чем за секунду,
     * кешируем уникальные значения, накопившиеся за секунду.
     */
    init {
        job = CoroutineScope(Dispatchers.IO).launchPeriodicAsync(1000) {
            if (processSet.isNotEmpty()) {
                barcodeScannerListener.onBarcodes(processSet.toList())
                processSet.clear()
            }
        }
        job.start()
    }


    override fun stop() {
        super.stop()
        barcodeScanner.close()
        job.cancel()
    }

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return barcodeScanner.process(image)
    }

    override fun onSuccess(results: List<Barcode>) {
        Log.d(TAG, results.toString())
        if (results.isNotEmpty()) {
            for (barcode in results) {
                barcode.displayValue?.let {
                    processSet.add(it)
                }
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed $e")
    }

    companion object {
        private const val TAG = "BarcodeProcessor"
    }

    interface BarcodeScannerListener {
        fun onBarcodes(results: List<String>)
    }
}
