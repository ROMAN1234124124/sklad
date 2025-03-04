package ru.altrimo.slad2025.viewmodel.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val RESULT_OK = "0"

open class BaseViewModel : ViewModel() {

    val viewShowLoading = MutableLiveData<Boolean>()
    val viewShowError = SingleLiveEvent<String>()

}