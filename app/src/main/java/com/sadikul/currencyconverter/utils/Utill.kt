package com.sadikul.currencyconverter.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Utill{
    private val TAG = Utill::class.java.simpleName

    fun processServerData(response: CurrencyResponse): List<CurrencyEntity> {
        Log.d(TAG, "Networking getData() Processing data")
        val list = mutableListOf<CurrencyEntity>()
        response.quotes?.let {
            var id = 0;
            for (data in it) {
                id++;
                data.apply {
                    val item = CurrencyEntity(
                        id,
                        data.key.substring(3),
                        data.value,
                        System.currentTimeMillis()
                    )
                    list.add(item)
                }
                //list.add(CurrencyEntity(++id,"USD",1.0))
            }
        }
        return list
    }

    fun convertMillsToTime(mills: Long): String{
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mills
        return formatter.format(calendar.time)
    }

    fun hideKeyboard(context: Context,view: View) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}