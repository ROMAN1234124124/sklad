package ru.altrimo.slad2025.data

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import javax.inject.Inject

class Preferences @Inject constructor(val preferences: SharedPreferences) {

    fun <T> put(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = preferences.getString(key, null)
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

}