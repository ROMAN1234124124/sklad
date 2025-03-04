package ru.altrimo.slad2025.network.request

import com.google.gson.annotations.SerializedName

data class BarcodeSearchRequest(
    @SerializedName("user_guid")
    val userGUID: String,
    var device: String,
    @SerializedName("doc_guid")
    val docGUID: String,
    @SerializedName("doc_version")
    val docVersion: Int,
    @SerializedName("BarcodesList")
    val barcodeList: List<Barcode>,
    @SerializedName("row_guid")
    val rowGUID: String? = null
)

data class Barcode(
    @SerializedName("Barcode")
    val barcode: String
)
