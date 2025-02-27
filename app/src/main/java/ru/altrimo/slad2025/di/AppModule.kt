package ru.altrimo.slad2025.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.altrimo.slad2025.data.Preferences
import ru.altrimo.slad2025.network.Api
import ru.altrimo.slad2025.network.base.RemoteDataSource
import ru.altrimo.slad2025.network.responce.LoginResponse
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRemoteApi(remoteDataSource: RemoteDataSource): Api {
        return remoteDataSource.buildApi(Api::class.java)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @SuppressLint("HardwareIds")
    @Singleton
    @Provides
    @Named("device")
    fun provideDevice(@ApplicationContext context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)


    @Singleton
    @Provides
    @Named("userGUID")
    fun provideUserGUID(preferences: Preferences): String =
        preferences.get<LoginResponse>(LoginResponse::class.java.name)?.userGUID ?: ""


}


