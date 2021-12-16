package com.sadikul.currencyconverter.ui.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.repository.CurrencyRepo
import com.sadikul.currencyconverter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repo: CurrencyRepo): ViewModel() {
    private val TAG = CurrencyViewModel::class.java.simpleName
    private val currencyMutableLiveData = MutableLiveData<Resource<MutableMap<String,Double>>>()
    val data: LiveData<Resource<MutableMap<String,Double>>>
        get() = currencyMutableLiveData

    fun getData(
        source: String,
        value: String) {
        viewModelScope.launch {
            Log.d(TAG,"Networking getting data getDataFromSever source $source value $value")
            repo.getData(
                source,
                value,
                currencyMutableLiveData
            )
        }
    }
}