package com.sadikul.currencyconverter.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sadikul.currencyconverter.R
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}