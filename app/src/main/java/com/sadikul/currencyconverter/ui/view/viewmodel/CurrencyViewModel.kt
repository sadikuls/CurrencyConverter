package com.sadikul.currencyconverter.ui.view.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import com.sadikul.currencyconverter.data.repository.CurrencyRepo
import com.sadikul.currencyconverter.utils.Resource
import kotlinx.coroutines.launch

class CurrencyViewModel @ViewModelInject constructor(private val repo: CurrencyRepo): ViewModel() {
    private val TAG = CurrencyViewModel::class.java.simpleName
    private val currencyMutableLiveData = MutableLiveData<Resource<List<CurrencyEntity>>>()
    val data: LiveData<Resource<List<CurrencyEntity>>>
        get() = currencyMutableLiveData

    fun getDataFromSever(
        access_key: String,
        source: String,
        value: String) {
        viewModelScope.launch {
            repo.getDataFromServer(
                access_key,
                source,
                value,
                currencyMutableLiveData
            )
        }
    }
}