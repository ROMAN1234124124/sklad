package ru.altrimo.slad2025.fragment.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.MainActivity
import java.lang.Error
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.functions


abstract class ViewBindingFragment<VB : ViewBinding> : Fragment() {


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
    protected inline fun <reified VB : ViewBinding> ViewBindingFragment<VB>.inflaterDelegate() =
        InflaterDelegate(VB::class)


    fun showError(error: String) {
        (requireActivity() as MainActivity).showError(error)
    }


    fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

}