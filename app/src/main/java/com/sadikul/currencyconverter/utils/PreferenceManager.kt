package com.sadikul.currencyconverter.utils

import android.content.Context

open class PreferenceManager(context: Context) : SharedPref(context) {
    companion object {
        private val mInstance: PreferenceManager? = null
        val TAG = PreferenceManager::class.java.simpleName
        fun getInstance(context: Context): PreferenceManager {
            return mInstance ?: PreferenceManager(context.applicationContext)
        }
    }
}
