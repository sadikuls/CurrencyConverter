package com.sadikul.currencyconverter.utils

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class MyApp: Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    val TAG = MyApp::class.java.simpleName

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}