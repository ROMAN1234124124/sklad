package ru.altrimo.slad2025.network.responce

import com.google.gson.annotations.SerializedName


data class Error(
    @SerializedName("result_usermess")
    val userMessage: String,
    @SerializedName("errorlist")
    val errorList: List<ErrorBlock>
)

data class ErrorBlock(
    val errorCode: String,
    val errorDescription: String
)

