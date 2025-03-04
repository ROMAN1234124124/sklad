package ru.altrimo.slad2025.network.responce

import com.google.gson.annotations.SerializedName

data class DocCloseResponse(
    @SerializedName("user_guid")
    val userGUID: String,
    var device: String,
    @SerializedName("doc_guid")
    val docGUID: String,
    @SerializedName("doc_version")
    val docVersion: Int,
    @SerializedName("user_message")
    val userMessage: String,
    val result: String,
    val error: Error
)