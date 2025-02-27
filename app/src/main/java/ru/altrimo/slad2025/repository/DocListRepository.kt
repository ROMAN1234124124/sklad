package ru.altrimo.slad2025.repository

import ru.altrimo.slad2025.network.Api
import ru.altrimo.slad2025.network.request.DocListRequest
import ru.altrimo.slad2025.repository.base.Repository
import javax.inject.Inject
import javax.inject.Named


class DocListRepository @Inject constructor(
    private val api: Api,
    @Named("userGUID") private var userGUID: String,
    @Named("device") private var device: String,
) : Repository(api) {


    suspend fun docList() = safeApiCall {
        api.docList(
            DocListRequest(
                userGUID = userGUID,
                device = device
            )
        )
    }.check()


}
