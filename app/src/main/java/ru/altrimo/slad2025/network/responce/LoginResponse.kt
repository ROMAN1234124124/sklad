package ru.altrimo.slad2025.network.responce

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val login: String,
    val pass: String,
    val device: String,
    @SerializedName("user_guid")
    val userGUID: String,
    val action: String,
    val result: String,
    val error: Error
)
