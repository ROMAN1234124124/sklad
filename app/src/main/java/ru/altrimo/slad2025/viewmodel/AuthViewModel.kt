package ru.altrimo.slad2025.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.altrimo.slad2025.data.local.Credential
import ru.altrimo.slad2025.network.request.LoginRequest
import ru.altrimo.slad2025.repository.AuthRepository
import ru.altrimo.slad2025.viewmodel.base.BaseViewModel
import ru.altrimo.slad2025.viewmodel.base.RESULT_OK
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @Named("device") private var device: String
) : BaseViewModel() {

    val viewResult = MutableLiveData<Unit>()
    val credential = MutableLiveData<Credential>()

    init {
        authRepository.getCredential()?.let {
            credential.postValue(it)
        }
    }

    fun auth(login: String, password: String) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                viewShowLoading.postValue(true)
                authRepository.auth(
                    LoginRequest(
                        login = login,
                        pass = password,
                        device = device
                    )
                )
            }.onSuccess {
                viewShowLoading.postValue(false)
                if (it.result == RESULT_OK) {
                    authRepository.putAuth(it)
                    viewResult.postValue(Unit)
                    authRepository.saveCredential(Credential(userName = login, password = password))
                } else {
                    viewShowError.postValue(it.error.userMessage)
                }
            }.onFailure {
                viewShowError.postValue(it.message)
                viewShowLoading.postValue(false)
            }

        }
    }

    fun rememberCredential(isRemember: Boolean) {
        authRepository.rememberCredential(isRemember)
    }


}

