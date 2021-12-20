package com.sadikul.currencyconverter.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pactice.hild_mvvm_room.dada.api.CurrencyApi
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.data.local.CurrencyDatabase
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.utils.Constants.ERROR_NO_INTERNET
import com.sadikul.currencyconverter.utils.NetworkHelper
import com.sadikul.currencyconverter.utils.Resource
import com.sadikul.currencyconverter.utils.Status
import com.sadikul.currencyconverter.utils.Utill
import com.sadikul.currencyconverter.worker.CurrencyDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            if(networkHelper.isNetworkConnected()){
                getRemoteData("USD", "1", currencyMutableLiveData)
            }else{
                currencyMutableLiveData?.postValue(Resource.error(ERROR_NO_INTERNET,null))
            }
        }else{
            Log.e(TAG,"Networking getData() Shoiwng local data updated at : ${Utill.convertMillsToTime(dataFromLocal.get(0).time!!)}")
            convertToMap(dataFromLocal).let {
                try {
                    val inputValue = value.toDouble()
                    currencyMutableLiveData?.postValue(Resource.success("data from local-db",convertCurrency(source, inputValue ,it)))
                }catch (ex: Exception){
                    Log.e(TAG,"Networking getData() error cause : ${ex.cause} message ${ex.message}")
                    var errMessage = "Error while doing conversion."
                    currencyMutableLiveData?.postValue(Resource.error(errMessage,null))
                }
            }
        }
        return true
    }

    private suspend fun getRemoteData(
        source: String,
        value: String,
        currencyMutableLiveData: MutableLiveData<Resource<MutableMap<String, Double>>>?
    ) {
        getDataFromServer (source, value){ response ->
            when (response.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "remote-data Data successfully got from server")
                    response.data?.let {
                        if (it.size > 0) {
                            CoroutineScope(Dispatchers.Default).launch {
                                withContext(Dispatchers.IO){
                                    clearDb()
                                    insertToDb(it)
                                }
                            }
                            val map = convertToMap(it)
                            Log.i(TAG, "map size ${map.size}")
                            map.let {
                                try {
                                    currencyMutableLiveData?.postValue(
                                        Resource.success(
                                            "Response received",
                                            convertCurrency(source, value.toDouble(), it)
                                        )
                                    )
                                } catch (ex: Exception) {
                                    currencyMutableLiveData?.postValue(
                                        Resource.error(
                                            "Something went wrong",
                                            null
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    Log.d(TAG, "remote-data failed to get data from server")
                    var message = ""
                    response.message?.let{
                        message = it
                    }
                    currencyMutableLiveData?.postValue(Resource.error(message, null))
                }
                else -> {
                    Log.d(TAG, "Something went wrong.")
                    currencyMutableLiveData?.postValue(Resource.error("Something went wrong.", null))
                }
            }
        }
    }

    suspend fun clearDb(){
        Log.d(TAG, "remote-data clearDb : clearing currency table")
        appDatabase.currencyDao().clearAll()
    }

    suspend fun insertToDb(items: List<CurrencyEntity>){
        Log.d(TAG, "remote-data insertToDb : inserting ${items.size} data to currency table")
        appDatabase.currencyDao().insertAll(items)
    }

    suspend fun getDataFromServer(source: String, value: String, result: (Resource<List<CurrencyEntity>>) -> Unit) {
        Log.i(TAG,"remote-data getDataFromServerWithCallback method called")
        if(networkHelper.isNetworkConnected()){
            val serverResponse = currencyApi.getData(
                BuildConfig.API_KEY,
                source,
                value
            )
            if(serverResponse.isSuccessful){
                try {
                    val data = Utill.processServerData(serverResponse.body()!!)
                    result(Resource.success("Successfully got the data",data))
                }catch (ex: Exception){

            }
            }else{
                result(Resource.error("Something went wrong.",null))
            }
        }else{
            result(Resource.error(ERROR_NO_INTERNET,null))
        }
    }


    private fun convertToMap(list: List<CurrencyEntity>): MutableMap<String,Double>{
        val map = mutableMapOf<String,Double>()
        for (item in list) {
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
        //Log.e(TAG,"currency conversion : source $source usdValueOfCurrency value $value currencyMap size ${currencyMap.size}")
        if(currencyMap.size > 0){
            val sourceCurrencyValue = currencyMap.get(source)!!
            val usdValueOfCurrency = value/sourceCurrencyValue
            //Log.e(TAG,"currency conversion : sourceCurrencyValue $sourceCurrencyValue usdValueOfCurrency $usdValueOfCurrency")
            currencyMap.forEach({
                val targetCurrencyValue = usdValueOfCurrency * it.value
                //Log.e(TAG,"currency conversion : currency ${it.key} value ${it.value} converted usd = $targetCurrencyValue")
                list.put(
                    it.key,
                    targetCurrencyValue
                )
            })
        }
        return list
    }
}