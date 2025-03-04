package ru.altrimo.slad2025.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.altrimo.slad2025.network.request.Barcode
import ru.altrimo.slad2025.network.responce.ContentDocResponse
import ru.altrimo.slad2025.network.responce.DocCloseResponse
import ru.altrimo.slad2025.repository.ContentDocRepository
import ru.altrimo.slad2025.viewmodel.base.BaseViewModel
import ru.altrimo.slad2025.viewmodel.base.RESULT_OK
import ru.altrimo.slad2025.viewmodel.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class ContentDocViewModel @Inject constructor(
    private val repository: ContentDocRepository
) : BaseViewModel() {

    val viewResult = MutableLiveData<ContentDocResponse>()
    val isShowCamera = MutableLiveData<Boolean>()
    val selectedProductGUID = MutableLiveData<String>()
    val closeDoc = SingleLiveEvent<DocCloseResponse>()
    val searchBarcode = SingleLiveEvent<Unit>()
    val deleteBarcode = SingleLiveEvent<Unit>()
    val deleteBarcodeAll = SingleLiveEvent<Unit>()

    init {
        isShowCamera.postValue(true)
    }

    fun changeShowCamera() {
        if (isShowCamera.value == false) {
            isShowCamera.postValue(true)
        } else {
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
                    viewResult.postValue(setSelectedRow(it))
                } else {
                    viewShowError.postValue(it.error.userMessage)
                }
            }.onFailure {
                viewShowError.postValue(it.message)
                viewShowLoading.postValue(false)
            }
        }
    }

    private fun setSelectedRow(contentDocResponse: ContentDocResponse): ContentDocResponse {
        contentDocResponse.listRowContainer.firstOrNull { product ->
            product.rowGUID == selectedProductGUID.value
        }?.isSelected = true
        return contentDocResponse
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

    fun deleteBarcodeAll(docGUID: String, docVersion: Int, rowGUID: String) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                repository.barcodeDeleteAll(
                    docGUID = docGUID,
                    docVersion = docVersion,
                    rowGUID = rowGUID
                )
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    deleteBarcodeAll.postValue(Unit)
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
                    barcodeList = barcodeList,
                    rowGUID = selectedProductGUID.value
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

    fun closeDoc(docVersion: Int, docGUID: String) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                repository.closeDoc(
                    docGUID = docGUID,
                    docVersion = docVersion
                )
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    closeDoc.postValue(it)
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