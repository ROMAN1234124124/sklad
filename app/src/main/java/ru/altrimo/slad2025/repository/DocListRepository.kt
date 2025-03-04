package ru.altrimo.slad2025.repository

import ru.altrimo.slad2025.network.Api
import ru.altrimo.slad2025.network.request.DocListRequest
import ru.altrimo.slad2025.network.request.DocOpenRequest
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


    suspend fun openDoc(docVersion: Int, docGUID: String) = safeApiCall {
        api.openDoc(
            DocOpenRequest(
                userGUID = userGUID,
                device = device,
                docVersion = docVersion,
                docGUID = docGUID
            )
        )
    }.check()

}
