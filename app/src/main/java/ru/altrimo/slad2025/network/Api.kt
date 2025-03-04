package ru.altrimo.slad2025.network

import retrofit2.http.Body
import retrofit2.http.POST
import ru.altrimo.slad2025.network.base.BaseWebServiceApi
import ru.altrimo.slad2025.network.request.BarcodeDeleteAllRequest
import ru.altrimo.slad2025.network.request.BarcodeDeleteRequest
import ru.altrimo.slad2025.network.request.BarcodeSearchRequest
import ru.altrimo.slad2025.network.request.ContentDocRequest
import ru.altrimo.slad2025.network.request.DocCloseRequest
import ru.altrimo.slad2025.network.request.DocListRequest
import ru.altrimo.slad2025.network.request.DocOpenRequest
import ru.altrimo.slad2025.network.request.LoginRequest
import ru.altrimo.slad2025.network.responce.BarcodeDeleteAllResponse
import ru.altrimo.slad2025.network.responce.BarcodeDeleteResponse
import ru.altrimo.slad2025.network.responce.BarcodeSearchResponse
import ru.altrimo.slad2025.network.responce.ContentDocResponse
import ru.altrimo.slad2025.network.responce.DocCloseResponse
import ru.altrimo.slad2025.network.responce.DocListResponse
import ru.altrimo.slad2025.network.responce.DocOpenResponse
import ru.altrimo.slad2025.network.responce.LoginResponse

interface Api : BaseWebServiceApi {

    @POST("authorize")
    suspend fun auth(@Body loginRequest: LoginRequest): LoginResponse

    @POST("docslist/check")
    suspend fun docList(@Body docList: DocListRequest): DocListResponse

    @POST("doc/content")
    suspend fun contentDoc(@Body contentDoc: ContentDocRequest): ContentDocResponse

    @POST("rowdoc/barcode_search")
    suspend fun barcodeSearch(@Body barcodeSearch: BarcodeSearchRequest): BarcodeSearchResponse

    @POST("rowdoc/barcode_delete")
    suspend fun barcodeDelete(@Body barcodeDelete: BarcodeDeleteRequest): BarcodeDeleteResponse

    @POST("rowdoc/barcode_deleteAll")
    suspend fun barcodeDeleteAll(@Body barcodeDelete: BarcodeDeleteAllRequest): BarcodeDeleteAllResponse

    @POST("doc/doc_open")
    suspend fun openDoc(@Body openDoc: DocOpenRequest): DocOpenResponse

    @POST("doc/doc_close")
    suspend fun closeDoc(@Body closetDoc: DocCloseRequest): DocCloseResponse
}