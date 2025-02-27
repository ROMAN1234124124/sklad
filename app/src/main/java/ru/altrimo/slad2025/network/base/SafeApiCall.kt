package ru.altrimo.slad2025.network.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface SafeApiCall {

    /**
     * Обертка над запросом ретрофита
     */
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.Default) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }

                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }

    /**
     * Кастует обертку в данные, если есть какая то ошибка - стреляет Exception
     */
    fun <T> Resource<T>.check(): T {
        when (this) {
            is Resource.Success -> {
                if (data == null) {
                    throw ApiExceptions.NetworkEmptyBodyException
                }

                return this.data
            }

            is Resource.Failure -> {
                if (this.isNetworkError) {
                    throw ApiExceptions.NetworkConnectionErrorException
                } else {
                    throw ApiExceptions.NetworkServerErrorException(this.errorCode, this.errorBody)
                }
            }

            else -> {
                throw ApiExceptions.NetworkUnknownErrorException
            }
        }
    }
}

