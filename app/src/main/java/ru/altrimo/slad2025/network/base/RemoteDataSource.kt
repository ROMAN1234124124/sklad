package ru.altrimo.slad2025.network.base

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.altrimo.slad2025.BuildConfig
import ru.altrimo.slad2025.data.Preferences
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val preferences: Preferences) {

    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(baseUrl())
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()
            .create(api)
    }


    private fun baseUrl() = preferences.apiServer ?: BuildConfig.API_URL

    private fun gson(): Gson =
        GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()


    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader(
                        "Authorization",
                        Credentials.basic(BuildConfig.API_USER, BuildConfig.API_PASSWORD)
                    )
                }.build())
            }.also { client ->
                client.connectTimeout(15, TimeUnit.SECONDS)
                client.readTimeout(15, TimeUnit.SECONDS)
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }

}

