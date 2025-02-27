package ru.altrimo.slad2025.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.altrimo.slad2025.network.responce.DocItem
import ru.altrimo.slad2025.repository.DocListRepository
import ru.altrimo.slad2025.viewmodel.base.BaseViewModel
import ru.altrimo.slad2025.viewmodel.base.RESULT_OK
import javax.inject.Inject

@HiltViewModel
class DocListViewModel @Inject constructor(
    private val repository: DocListRepository
) : BaseViewModel() {

    val viewResult = MutableLiveData<List<DocItem>>()

    init {
        docList()
    }

    fun docList() {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                repository.docList()
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    viewResult.postValue(it.docItem)
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

