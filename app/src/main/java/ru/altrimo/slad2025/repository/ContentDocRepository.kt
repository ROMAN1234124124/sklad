package ru.altrimo.slad2025.repository

import ru.altrimo.slad2025.network.Api
import ru.altrimo.slad2025.network.request.Barcode
import ru.altrimo.slad2025.network.request.BarcodeDeleteRequest
import ru.altrimo.slad2025.network.request.BarcodeSearchRequest
import ru.altrimo.slad2025.network.request.ContentDocRequest
import ru.altrimo.slad2025.repository.base.Repository
import javax.inject.Inject
import javax.inject.Named

class ContentDocRepository @Inject constructor(
    private val api: Api,
    @Named("userGUID") private var userGUID: String,
    @Named("device") private var device: String,
) : Repository(api) {

    suspend fun contentDoc(docGUID: String, docVersion: Int) = safeApiCall {
        api.contentDoc(
            ContentDocRequest(
                userGUID = userGUID,
                device = device,
                docGUID = docGUID,
                docVersion = docVersion
            )
        )
    }.check()

    suspend fun barcodeSearch(docGUID: String, docVersion: Int, barcodeList: List<Barcode>) =
        safeApiCall {
            api.barcodeSearch(
                BarcodeSearchRequest(
                    userGUID = userGUID,
                    device = device,
                    docGUID = docGUID,
                    docVersion = docVersion,
                    barcodeList = barcodeList
                )
            )
        }.check()

    suspend fun barcodeDelete(docGUID: String, docVersion: Int, barcode: String) = safeApiCall {
        api.barcodeDelete(
            BarcodeDeleteRequest(
                userGUID = userGUID,
                device = device,
                docGUID = docGUID,
                docVersion = docVersion,
                barcode = barcode
            )
        )
    }.check()

}