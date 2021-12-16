package com.sadikul.currencyconverter.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pactice.hild_mvvm_room.dada.api.CurrencyApi
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.data.local.LocalDatabase
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import com.sadikul.currencyconverter.utils.NetworkHelper
import com.sadikul.currencyconverter.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class CurrencyRepo @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val appDatabase: LocalDatabase,
    private val networkHelper: NetworkHelper
){
    companion object{
        private val TAG = CurrencyRepo::class.java.simpleName
    }

    suspend fun getData(
        source: String,
        value: String,
        currencyMutableLiveData: MutableLiveData<Resource<MutableMap<String,Double>>>
    ){
        var dataFromLocal = appDatabase.currencyDao().getAll()

        if(dataFromLocal.size == 0){
            Log.e(TAG,"Networking getData() Database is empty")
            getDataFromServer(source, value, currencyMutableLiveData)
        }else{
            Log.e(TAG,"Networking getData() Shoiwng local data")
            currencyMutableLiveData.postValue(Resource.success("data from local-db",convertCurrency(source, value.toInt() ,convertToMap(dataFromLocal))))
        }
    }

    private suspend fun getDataFromServer(source: String, value: String, currencyMutableLiveData: MutableLiveData<Resource<MutableMap<String,Double>>>) {
        val defaultValue = 1
        if(networkHelper.isNetworkConnected()){
            val serverResponse = currencyApi.getData(
                BuildConfig.API_KEY,
                source.substring(3),
                defaultValue.toString()
            )
            if(serverResponse.isSuccessful){
                val data = processServerData(serverResponse.body()!!)
                data.let {
                    val map = convertToMap(it)
                    Log.i(TAG,"map size ${map.size}")
                    currencyMutableLiveData.postValue(Resource.success("Response received",convertCurrency(source,value.toInt(),map)))
                    insertIntoDb(it)
                }
            }else{
                currencyMutableLiveData.postValue(Resource.error("Error",null))
            }
        }
    }

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
                        data.key,
                        data.value
                    )
                    list.add(item)
                }
            }
        }
        return list
    }

    private fun convertToMap(list: List<CurrencyEntity>): MutableMap<String,Double>{
        val map = mutableMapOf<String,Double>()
        for (item in list) {
            Log.i(TAG,"convertToMap key ${item.currency} value ${item.value}")
            map.put(item.currency!!, item.value!!)
        }
        return map
    }

    private fun convertCurrency(
        source: String,
        value: Int,
        currencyMap: MutableMap<String,Double>
    ): MutableMap<String,Double>{
        Log.e(TAG,"currency conversion : source $source usdValueOfCurrency value $value")
        val list = mutableMapOf<String,Double>()
        currencyMap.get(source)?.let{

        }
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
        return list
    }


/*    private fun getAllFromDb(): List<CurrencyEntity> {
        Log.d(TAG,"Networking getDataFromDb() getting from local db")
        CoroutineScope(Dispatchers.Default).launch {

            try {
                val localData  = processLocalData(appDatabase.currencyDao().getAll())

                liveData.postValue(Resource.success("data from local-db",appDatabase.currencyDao().getAll()))
            }catch (exp: Exception){
                liveData.postValue(Resource.error("Error on getting data from db",null))
            }
        }
    }*/

    private fun insertIntoDb(list: List<CurrencyEntity>){
        CoroutineScope(Dispatchers.Main).launch {
            list.apply {
                val insertionProcessDone = withContext(Dispatchers.IO){
                    try{
                        appDatabase.currencyDao().insertAll(list)
                        true
                    }catch (exp: Exception){
                        false
                    }
                }
                if(insertionProcessDone){
                    Log.e(TAG,"All data inserted")
                }
            }
        }
    }
}