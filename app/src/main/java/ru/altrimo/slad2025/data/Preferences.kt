package ru.altrimo.slad2025.data

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import ru.altrimo.slad2025.BuildConfig
import javax.inject.Inject


class Preferences @Inject constructor(val preferences: SharedPreferences) {

    private companion object {
        const val API_SERVER_PREF = "api_serv_pref"
        const val IS_COMPUTER_VISION = "is_computer_vision"
        const val REMEMBER_CREDENTIAL_PREF = "remember_credential_pref"
    }

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

    var isRememberCredential: Boolean
        get() = preferences.getBoolean(REMEMBER_CREDENTIAL_PREF, false)
        set(value) {
            preferences.edit().putBoolean(REMEMBER_CREDENTIAL_PREF, value).apply()
        }

    var isComputerVision: Boolean
        get() = preferences.getBoolean(IS_COMPUTER_VISION, false)
        set(value) {
            preferences.edit().putBoolean(IS_COMPUTER_VISION, value).apply()
        }

}
