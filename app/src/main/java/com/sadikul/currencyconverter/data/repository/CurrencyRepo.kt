package com.sadikul.currencyconverter.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pactice.hild_mvvm_room.dada.api.CurrencyApi
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.data.local.CurrencyDatabase
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.utils.NetworkHelper
import com.sadikul.currencyconverter.utils.Resource
import com.sadikul.currencyconverter.utils.Utill
import java.lang.Exception
import javax.inject.Inject

class CurrencyRepo @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val appDatabase: CurrencyDatabase,
    private val networkHelper: NetworkHelper
){
    companion object{
        private val TAG = CurrencyRepo::class.java.simpleName
    }

    suspend fun getData(
        source: String,
        value: String,
        currencyMutableLiveData: MutableLiveData<Resource<MutableMap<String, Double>>>?
    ):Boolean{
        if(source.equals("") || value.equals("")) return false
        currencyMutableLiveData?.postValue(Resource.loading(null))
        var dataFromLocal = appDatabase.currencyDao().getAll()

        if(dataFromLocal.size == 0){
            Log.e(TAG,"Networking getData() Database is empty")
            getDataFromServer(source, value, currencyMutableLiveData!!)
        }else{
            Log.e(TAG,"Networking getData() Shoiwng local data")
            convertToMap(dataFromLocal).let {
                try {
                    val inputValue = value.toDouble()
                    currencyMutableLiveData?.postValue(Resource.success("data from local-db",convertCurrency(source, inputValue ,it)))
                }catch (ex: Exception){
                    Log.e(TAG,"Networking getData() error cause : ${ex.cause} message ${ex.message}")
                    var errMessage = "Something went wrong."
                    ex.message?.let{
                        if(it.contains("For input string")){
                            errMessage = "Could not convert."
                        }
                    }
                    currencyMutableLiveData?.postValue(Resource.error(errMessage,null))
                }
            }
        }
        return true
    }

    private suspend fun getDataFromServer(source: String, value: String, currencyMutableLiveData: MutableLiveData<Resource<MutableMap<String,Double>>>) {
        val defaultValue = 1
        if(networkHelper.isNetworkConnected()){
            val serverResponse = currencyApi.getData(
                BuildConfig.API_KEY,
                source,
                defaultValue.toString()
            )
            if(serverResponse.isSuccessful){
                //val data = processServerData(serverResponse.body()!!)
                val data = Utill.processServerData(serverResponse.body()!!)
                data.let {
                    if(it.size > 0){
                        val map = convertToMap(it)
                        Log.i(TAG,"map size ${map.size}")
                        map.let {
                            try{
                                currencyMutableLiveData.postValue(Resource.success("Response received",convertCurrency(source,value.toDouble(),it)))
                            }catch(ex: Exception){
                                currencyMutableLiveData.postValue(Resource.error("Something went wrong",null))
                            }
                        }
                        Utill.insertIntoDb(appDatabase, it)
                        appDatabase.currencyDao().clearAll()
                        appDatabase.currencyDao().insertAll(it)
                    }
                }
            }else{
                currencyMutableLiveData.postValue(Resource.error("Error",null))
            }
        }
    }

/*
    private fun processServerData(response: CurrencyResponse): List<CurrencyEntity> {
        Log.d(TAG,"Networking getData() Processing data")
        val list = mutableListOf<CurrencyEntity>()
        response.quotes?.let {
            var id = 0;
            for (data in it) {
                id++;
                data.apply {
                    val item = CurrencyEntity(
                        id,
                        data.key.substring(3),
                        data.value
                    )
                    list.add(item)
                }
                list.add(CurrencyEntity(++id,"USD",1.0))
            }
        }
        return list
    }
*/

    private fun convertToMap(list: List<CurrencyEntity>): MutableMap<String,Double>{
        val map = mutableMapOf<String,Double>()
        for (item in list) {
            Log.i(TAG,"convertToMap id $item.id" +
                    "key ${item.currency} value ${item.value}}")
            item.time?.let {
                Log.i(TAG,"convertToMap ${Utill.convertMillsToTime(it)}}")
            }
            map.put(item.currency!!, item.value!!)
        }
        return map
    }

    private fun convertCurrency(
        source: String,
        value: Double,
        currencyMap: MutableMap<String,Double>
    ): MutableMap<String,Double>{

        val list = mutableMapOf<String,Double>()
        Log.e(TAG,"currency conversion : source $source usdValueOfCurrency value $value currencyMap size ${currencyMap.size}")
        if(currencyMap.size > 0){
            val sourceCurrencyValue = currencyMap.get(source)!!
            val usdValueOfCurrency = value/sourceCurrencyValue
            Log.e(TAG,"currency conversion : sourceCurrencyValue $sourceCurrencyValue usdValueOfCurrency $usdValueOfCurrency")
            currencyMap.forEach({
                val targetCurrencyValue = usdValueOfCurrency * it.value
                Log.e(TAG,"currency conversion : currency ${it.key} value ${it.value} converted usd = $targetCurrencyValue")
                list.put(
                    it.key,
                    targetCurrencyValue
                )
            })
        }
        return list
    }
}