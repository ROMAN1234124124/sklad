package ru.altrimo.slad2025.network

import retrofit2.http.Body
import retrofit2.http.POST
import ru.altrimo.slad2025.network.request.DocListRequest
import ru.altrimo.slad2025.network.request.LoginRequest
import ru.altrimo.slad2025.network.responce.DocListResponse
import ru.altrimo.slad2025.network.responce.LoginResponse

interface Api : BaseWebServiceApi {

    @POST("authorize")
    suspend fun auth(@Body loginRequest: LoginRequest): LoginResponse

    @POST("docslist/check")
    suspend fun docList(@Body docList: DocListRequest): DocListResponse

}