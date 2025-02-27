package ru.altrimo.slad2025.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.altrimo.slad2025.network.request.Barcode
import ru.altrimo.slad2025.network.responce.ContentDocResponse
import ru.altrimo.slad2025.repository.ContentDocRepository
import ru.altrimo.slad2025.viewmodel.base.BaseViewModel
import ru.altrimo.slad2025.viewmodel.base.RESULT_OK
import javax.inject.Inject

@HiltViewModel
class ContentDocViewModel @Inject constructor(
    private val repository: ContentDocRepository
) : BaseViewModel() {

    val viewResult = MutableLiveData<ContentDocResponse>()
    val searchBarcode = MutableLiveData<Unit>()
    val deleteBarcode = MutableLiveData<Unit>()
    val isShowCamera = MutableLiveData<Boolean>()


    init {
        isShowCamera.postValue(false)
    }

    fun changeShowCamera() {
        if (isShowCamera.value == false){
            isShowCamera.postValue(true)
        }else{
            isShowCamera.postValue(false)
        }
    }

    fun contentDoc(docGUID: String, docVersion: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                repository.contentDoc(
                    docGUID = docGUID,
                    docVersion = docVersion
                )
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    viewResult.postValue(it)
                } else {
                    viewShowError.postValue(it.error.userMessage)
                }
            }.onFailure {
                viewShowError.postValue(it.message)
                viewShowLoading.postValue(false)
            }
        }
    }

    fun deleteBarcode(docGUID: String, docVersion: Int, barcode: String) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                repository.barcodeDelete(
                    docGUID = docGUID,
                    docVersion = docVersion,
                    barcode = barcode
                )
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    deleteBarcode.postValue(Unit)
                } else {
                    viewShowError.postValue(it.error.userMessage)
                }
            }.onFailure {
                viewShowError.postValue(it.message)
                viewShowLoading.postValue(false)
            }
        }
    }


    fun searchBarcode(docGUID: String, docVersion: Int, barcodeList: List<Barcode>) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                repository.barcodeSearch(
                    docGUID = docGUID,
                    docVersion = docVersion,
                    barcodeList = barcodeList
                )
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    searchBarcode.postValue(Unit)
                } else {
                    viewShowError.postValue(it.error.userMessage)
                }
            }.onFailure {
                viewShowError.postValue(it.message)
                viewShowLoading.postValue(false)
            }
        }
    }

}