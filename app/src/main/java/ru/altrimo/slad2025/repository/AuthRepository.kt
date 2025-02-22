package ru.altrimo.slad2025.repository

import ru.altrimo.slad2025.data.Preferences
import ru.altrimo.slad2025.network.Api
import ru.altrimo.slad2025.network.request.LoginRequest
import ru.altrimo.slad2025.network.responce.LoginResponse
import ru.altrimo.slad2025.repository.base.Repository
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val api: Api,
    private val preferences: Preferences
) : Repository(api) {

    suspend fun auth(loginRequest: LoginRequest) = safeApiCall {
        api.auth(loginRequest)
    }.check()


    fun putAuth(loginResponse: LoginResponse) {
        preferences.put(loginResponse, loginResponse::class.java.name)
    }

}
