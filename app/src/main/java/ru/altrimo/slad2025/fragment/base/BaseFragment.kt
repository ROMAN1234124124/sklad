package ru.altrimo.slad2025.fragment.base

import android.content.Context
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

open class BaseFragment : Fragment(), KeyListener {

    private var keyListener: KeyListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    setKeyDownListener(this@BaseFragment)
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    setKeyDownListener(null)
                }
            }
        })
    }

    fun setKeyDownListener(listener: KeyListener?) {
        keyListener = listener
    }

    fun handleKeyDown(keyCode: Int) {
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> keyListener?.actionDel()
        }

    }

    override fun actionDel() = Unit

}