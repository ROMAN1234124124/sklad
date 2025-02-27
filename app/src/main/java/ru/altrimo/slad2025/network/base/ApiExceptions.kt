package ru.altrimo.slad2025.network.base

import okhttp3.ResponseBody
import java.lang.Exception

/**
 * Все возможные виды сетевых исключений
 *
 * Пробрасываются при вызове check() у safeApiCall()
 */

sealed interface ApiExceptions {
    // Сервер вернул ошибку
    class NetworkServerErrorException(val errorCode: Int?, val errorBody: ResponseBody?) :
        Exception("$errorCode: $errorBody"), ApiExceptions

    // Ошибка подключения к сети
    object NetworkConnectionErrorException : Exception("Connection error exception"), ApiExceptions

    // Неизвестная ошибка
    object NetworkUnknownErrorException : Exception("Unknown error"), ApiExceptions

    // Ошибка пустого тела ответа
    object NetworkEmptyBodyException : Exception("Empty body"), ApiExceptions
}
