package ru.altrimo.slad2025.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.functions

/**
 * Базовый диалог, который позволяет быстро заинфлэйтить ViewBinding [VB]
 *
 * Автоматически инициализирует [VB] - ViewBinding класс в переменную [binding]
 * Так же очищает память при уничтожении фрагмента
 *
 * Как испольвоать:
 * - Наследуемся от этого фрагмента с указанием биндинга
 * - Переопределяем: val inflaterDelegate by inflaterDelegate()
 * - Пользуемся
 *
 * Фрагмент полностью проинициализирован в методе [init]
 */
abstract class ViewBindingDialog<VB : ViewBinding> : BaseDialogFragment(){
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
        ReadOnlyProperty<ViewBindingDialog<VB>, (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB> {
        override fun getValue(
            thisRef: ViewBindingDialog<VB>,
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
    protected inline fun <reified VB : ViewBinding> ViewBindingDialog<VB>.inflaterDelegate() =
        InflaterDelegate(VB::class)
}