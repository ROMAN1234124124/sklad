package ru.altrimo.slad2025.atol

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


abstract class BarcodeReceiver : BroadcastReceiver() {

    companion object {
        const val SCAN_DECODING_BROADCAST = "com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST"
        const val SCAN_DECODING_DATA = "EXTRA_BARCODE_DECODING_DATA"
        const val SCAN_SYMBOLOGY_TYPE = "EXTRA_BARCODE_DECODING_SYMBOLE"
    }

    private var context: Context? = null

    fun registerLifecycleEventObserver(context: Context) =
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                register(context)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                unregister(context)
            }
        }


    override fun onReceive(context: Context, intent: Intent) {
        when {
            intent.action == SCAN_DECODING_BROADCAST -> if (intent.hasExtra(SCAN_DECODING_DATA)) {
                val type = if (intent.hasExtra(SCAN_SYMBOLOGY_TYPE))
                    intent.getStringExtra(SCAN_SYMBOLOGY_TYPE)
                else
                    "N/A"
                val barcode = Barcode(
                    type ?: "N/A",
                    intent.getStringExtra(SCAN_DECODING_DATA) ?: "N/A"
                )
                onBarcodeReceive(context, barcode)
            }

        }
    }

    private fun register(context: Context) {
        this.context = context
        val intentFilter = IntentFilter(SCAN_DECODING_BROADCAST)
        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, intentFilter, RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(this, intentFilter)
        }
    }

    private fun unregister(context: Context) {
        this.context = null
        context.unregisterReceiver(this)
    }

    protected abstract fun onBarcodeReceive(context: Context, barcode: Barcode)
}
