package ru.altrimo.slad2025.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.altrimo.slad2025.network.request.DocListRequest
import ru.altrimo.slad2025.network.responce.DocItem
import ru.altrimo.slad2025.repository.DocListRepository
import ru.altrimo.slad2025.viewmodel.base.BaseViewModel
import ru.altrimo.slad2025.viewmodel.base.RESULT_OK
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DocListViewModel @Inject constructor(
    private val docListRepository: DocListRepository,
    @Named("device") private var device: String
) : BaseViewModel() {

    val viewResult = MutableLiveData<List<DocItem>>()

    init {
        docList()
    }

    fun docList() {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                docListRepository.docList(
                    DocListRequest(
                        userGUID = docListRepository.getGUID(),
                        device = device
                    )
                )
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

