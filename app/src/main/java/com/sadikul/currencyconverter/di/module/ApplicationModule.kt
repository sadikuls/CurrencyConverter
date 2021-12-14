package com.sadikul.currencyconverter.di.module

import android.content.Context
import android.util.Log
import com.pactice.hild_mvvm_room.dada.api.ApiHelper
import com.pactice.hild_mvvm_room.dada.api.ApiHelperImpl
import com.pactice.hild_mvvm_room.dada.api.ApiService
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.data.local.LocalDatabase
import com.sadikul.currencyconverter.utils.Constants
import com.sadikul.currencyconverter.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal class ApplicationModule {
    private val TAG = ApplicationModule::class.java.simpleName

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    fun provideTimeOutLimit() = Constants.TIME_OUT_LIMIT

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext appContext: Context): PreferenceManager = PreferenceManager.getInstance(appContext)

    @Provides
    @Singleton
    fun provideOkHttpClient(timeOutLimit: Long) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .connectTimeout(timeOutLimit, TimeUnit.SECONDS)
            .writeTimeout(timeOutLimit, TimeUnit.SECONDS)
            .readTimeout(timeOutLimit, TimeUnit.SECONDS)
            .build()
    } else OkHttpClient
        .Builder()
        .connectTimeout(timeOutLimit, TimeUnit.SECONDS)
        .writeTimeout(timeOutLimit, TimeUnit.SECONDS)
        .readTimeout(timeOutLimit, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit {
        Log.d(TAG, "networking BASE_URL $BASE_URL")
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) = LocalDatabase.getInstance(context = appContext)
}