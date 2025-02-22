package ru.altrimo.slad2025.repository

import ru.altrimo.slad2025.data.Preferences
import ru.altrimo.slad2025.network.Api
import ru.altrimo.slad2025.network.request.DocListRequest
import ru.altrimo.slad2025.network.responce.LoginResponse
import ru.altrimo.slad2025.repository.base.Repository
import javax.inject.Inject


class DocListRepository @Inject constructor(
    private val api: Api,
    private val preferences: Preferences
) : Repository(api) {


    suspend fun docList(docListRequest: DocListRequest) = safeApiCall {
        api.docList(docListRequest)
    }.check()

    fun getGUID() =
        preferences.get<LoginResponse>(LoginResponse::class.java.name)?.userGUID ?: ""


}
