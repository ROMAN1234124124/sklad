package ru.altrimo.slad2025.network.request

import com.google.gson.annotations.SerializedName

data class DocListRequest(
    @SerializedName("user_guid")
    val userGUID: String,
    var device: String
)
