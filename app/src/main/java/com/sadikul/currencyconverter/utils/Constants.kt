package com.sadikul.currencyconverter.utils

import com.sadikul.currencyconverter.BuildConfig

object Constants {
    const val DATABASE_NAME = "Currency.db"
    val PREFERENCE= BuildConfig.APPLICATION_ID + ".PREFERENCE";
    const val TIME_OUT_LIMIT : Long = 180
    const val PERIODIC_WORK_INTERVAL : Long = 30
    const val PERIODIC_WORK_INITIAL_DELAY : Long = 30
    const val PERIODIC_WORK_TAG : String = "currencyDataWork"
    const val PERIODIC_WORK_NAME : String = "preodicCurrencyData"
}