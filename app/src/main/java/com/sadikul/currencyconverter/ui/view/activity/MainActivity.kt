package com.sadikul.currencyconverter.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TimeUtils
import android.widget.Toast
import androidx.work.*
import com.sadikul.currencyconverter.R
import com.sadikul.currencyconverter.utils.Constants.PERIODIC_WORK_NAME
import com.sadikul.currencyconverter.worker.CurrencyDataWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}