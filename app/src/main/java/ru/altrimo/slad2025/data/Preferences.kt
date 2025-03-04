package ru.altrimo.slad2025.data

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.GsonBuilder
import ru.altrimo.slad2025.BuildConfig
import javax.inject.Inject

const val API_SERVER_PREF = "api_serv_pref"

class Preferences @Inject constructor(val preferences: SharedPreferences) {

    fun <T> put(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = preferences.getString(key, null)
        return GsonBuilder().create().fromJson(value, T::class.java)
    }


    var apiServer: String?
        get() = preferences.getString(API_SERVER_PREF, BuildConfig.API_URL)
        @SuppressLint("ApplySharedPref")
        set(value) {
            preferences.edit().putString(API_SERVER_PREF, value).commit()
        }

}
