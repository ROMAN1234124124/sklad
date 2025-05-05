package ru.altrimo.slad2025.fragment.base

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.activity.MainActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.functions


abstract class ViewBindingFragment<VB : ViewBinding> : BaseFragment() {


    private var _binding: ViewBinding? = null
    abstract val inflaterDelegate: (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = inflaterDelegate.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInflationComplete()
    }

    open fun onInflationComplete() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Делегат, который с помощью рефлексии находит метод inflate() для сгенерированного класса ViewBinding-а и вызывает его
     */
    protected class InflaterDelegate<VB : ViewBinding>(val kclass: KClass<VB>) :
        ReadOnlyProperty<ViewBindingFragment<VB>, (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB> {
        override fun getValue(
            thisRef: ViewBindingFragment<VB>,
            property: KProperty<*>
        ): (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB {
            return { inflater, container, attachToRoot ->
                @Suppress("UNCHECKED_CAST")
                kclass.functions.find { it.name == "inflate" && it.parameters.size == 3 }
                    ?.call(inflater, container, attachToRoot) as VB
            }
        }
    }

    /**
     * Вспомогательная функция для упрощенного вызова InflaterDelegate()
     */
    protected inline fun <reified VB : ViewBinding> inflaterDelegate() =
        InflaterDelegate(VB::class)


    fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    fun permissionLauncher(onResultReady: (Boolean) -> Unit) =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            onResultReady.invoke(result)
        }


    fun showError(message: String) {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle(R.string.alertDialogErrorTitle)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.OK) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    fun showConfirmationDialog(
        message: String,
        positiveAction: () -> Unit
    ) {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle(getString(R.string.alertDialogMessageTitle))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.OK) { dialog, _ ->
                positiveAction()
                dialog.cancel()
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun showNextDialog(
        message: String,
        positiveAction: () -> Unit
    ) {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle(getString(R.string.alertNextDialogMessageTitle))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.OK) { dialog, _ ->
                positiveAction()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun playSoundError() {
        val afd = requireContext().resources.openRawResourceFd(R.raw.error) ?: return
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mediaPlayer.setOnPreparedListener { it.start() }
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.prepareAsync()
        vibrate()
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(VibratorManager::class.java)
            val vibrator = vibratorManager.defaultVibrator
            if (vibrator.hasVibrator()) {
                val vibrationEffect =
                    VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        } else {
            @Suppress("DEPRECATION")
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect =
                        VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(vibrationEffect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        }
    }
}

