package ru.altrimo.slad2025.network

import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()
}