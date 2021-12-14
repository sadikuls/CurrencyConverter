package com.sadikul.currencyconverter.utils

import android.content.Context
import android.content.SharedPreferences
import com.sadikul.currencyconverter.utils.Constants.PREFERENCE

open class SharedPref(context: Context) {

    var mContext: Context? = null
    var mSharedPreference: SharedPreferences? = null

    init {
        this.mContext = context
        mSharedPreference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
    }


    /***
     * @param key : key for shared preference
     * @param value : String value for respective key
     * @return return true if sucessfully write to preference
     */
    fun writeStringToPref(key: String, value: String): Boolean {
        val editor = mSharedPreference!!.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    /***
     * @param key : key for shared preference
     * @param value : int value for respective key
     * @return return true if sucessfully write to preference
     */
    fun writeIntToPref(key: String, value: Int): Boolean {
        val editor = mSharedPreference!!.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    /***
     * @param key : key for shared preference
     * @param value : bool value for respective key
     * @return return true if sucessfully write to preference
     */
    fun writeBoolToPref(key: String, value: Boolean): Boolean {
        val editor = mSharedPreference!!.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    /***
     * @param key : key of respective value
     * @param default_value : default string if value is null
     * @return return default string if value is null
     */
    fun getString(key: String, default_value: String): String? {
        return mSharedPreference!!.getString(key, default_value)
    }

    /***
     * @param key : key of respective value
     * @return return false if value is null
     */
    fun getBoolean(key: String): Boolean {
        return mSharedPreference!!.getBoolean(key, false)
    }

    /***
     * @param key : key of respective value
     * @return return -1 if value is null
     */
    fun getInt(key: String): Int {
        return mSharedPreference?.getInt(key, 0)!!
    }
}