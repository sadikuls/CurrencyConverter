package com.sadikul.currencyconverter.di.module

import android.content.Context
import android.util.Log
import androidx.work.*
import com.pactice.hild_mvvm_room.dada.api.CurrencyApi
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.data.local.CurrencyDatabase
import com.sadikul.currencyconverter.data.repository.CurrencyRepo
import com.sadikul.currencyconverter.utils.Constants
import com.sadikul.currencyconverter.utils.Constants.PERIODIC_WORK_INITIAL_DELAY
import com.sadikul.currencyconverter.utils.Constants.PERIODIC_WORK_INTERVAL
import com.sadikul.currencyconverter.utils.Constants.PERIODIC_WORK_TAG
import com.sadikul.currencyconverter.utils.NetworkHelper
import com.sadikul.currencyconverter.utils.PreferenceManager
import com.sadikul.currencyconverter.worker.CurrencyDataWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ApplicationModule {
    private val TAG = ApplicationModule::class.java.simpleName

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL
/*
    @Provides
    fun provideApiKeY() = BuildConfig.API_KEY*/

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
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi = retrofit.create(CurrencyApi::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) = CurrencyDatabase.getInstance(context = appContext)

    @Provides
    fun provideWorkRequest(): PeriodicWorkRequest{
        return PeriodicWorkRequestBuilder<CurrencyDataWorker>(PERIODIC_WORK_INTERVAL, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .setRequiresBatteryNotLow(true)
                .build())
            .setInitialDelay(PERIODIC_WORK_INITIAL_DELAY,TimeUnit.SECONDS)
            .addTag(PERIODIC_WORK_TAG)
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager = WorkManager.getInstance(
        appContext
    )

    @Provides
    @Singleton
    fun provideCurrencyRepo(
        database: CurrencyDatabase,
        networkHelper: NetworkHelper,
        currencyApi: CurrencyApi
    ):CurrencyRepo = CurrencyRepo(currencyApi, database, networkHelper)
}