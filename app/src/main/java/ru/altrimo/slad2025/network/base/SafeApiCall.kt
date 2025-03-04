package ru.altrimo.slad2025.network.base

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

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
                        Resource.Failure(true, throwable.code(), throwable.response()?.errorBody())
                    }

                    is IllegalArgumentException -> {
                        Resource.Failure(
                            true,
                            1,
                            ResponseBody.create(
                                null,
                                throwable.message ?: "IllegalArgumentException"
                            )
                        )
                    }

                    is JsonSyntaxException -> {
                        Resource.Failure(
                            true,
                            1,
                            ResponseBody.create(null, throwable.message ?: "JsonSyntaxException")
                        )
                    }

                    is SocketTimeoutException -> {
                        Resource.Failure(
                            true,
                            1,
                            ResponseBody.create(null, throwable.message ?: "SocketTimeoutException")
                        )
                    }

                    is IOException -> {
                        Resource.Failure(
                            true,
                            1,
                            ResponseBody.create(null, throwable.message ?: "IOException")
                        )
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

