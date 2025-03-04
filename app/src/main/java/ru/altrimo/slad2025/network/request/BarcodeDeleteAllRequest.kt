package ru.altrimo.slad2025.network.request

import com.google.gson.annotations.SerializedName


data class BarcodeDeleteAllRequest(
    @SerializedName("user_guid")
    val userGUID: String,
    var device: String,
    @SerializedName("doc_guid")
    val docGUID: String,
    @SerializedName("doc_version")
    val docVersion: Int,
    @SerializedName("row_guid")
    val rowGUID: String
)
