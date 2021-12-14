package com.sadikul.currencyconverter.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pactice.hild_mvvm_room.dada.api.ApiHelper
import com.pactice.hild_mvvm_room.dada.api.ApiService
import com.sadikul.currencyconverter.data.local.LocalDatabase
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import com.sadikul.currencyconverter.utils.NetworkHelper
import com.sadikul.currencyconverter.utils.Resource
import javax.inject.Inject

class CurrencyRepo @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: LocalDatabase,
    private val networkHelper: NetworkHelper
){
    private val TAG = CurrencyRepo::class.java.simpleName

    suspend fun getDataFromServer(
        access_key: String,
        source: String,
        value: String,
        currencyMutableLiveData: MutableLiveData<Resource<List<CurrencyEntity>>>
    ){
        if(networkHelper.isNetworkConnected()){
            val serverResponse = apiService.getData(
                access_key,
                source,
                value
            )
            if(serverResponse.isSuccessful){
                currencyMutableLiveData.postValue(Resource.success("Response received",processData(serverResponse.body()!!)))
            }else{
                currencyMutableLiveData.postValue(Resource.error("Error",null))
            }
        }
    }

    private fun processData(response: CurrencyResponse): List<CurrencyEntity> {
        Log.d(TAG,"Networking getImages() Processing data")
        val list = mutableListOf<CurrencyEntity>()
        response.quotes?.let {
            for (data in it) {
                data.apply {
                    val item = CurrencyEntity(
                        data.key,
                        data.value.toInt(),
                        0
                    )
                    list.add(item)
                }
            }
        }
        return list
    }
}