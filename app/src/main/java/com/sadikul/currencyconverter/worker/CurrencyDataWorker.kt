package com.sadikul.currencyconverter.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pactice.hild_mvvm_room.dada.api.CurrencyApi
import com.sadikul.currencyconverter.BuildConfig
import com.sadikul.currencyconverter.data.local.LocalDatabase
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import com.sadikul.currencyconverter.utils.NetworkHelper
import com.sadikul.currencyconverter.utils.Utill
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.lang.Exception

@HiltWorker
class CurrencyDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkHelper: NetworkHelper,
    private val currencyApi: CurrencyApi,
    private val appDatabase: LocalDatabase
) : CoroutineWorker(appContext, workerParams) {

    companion object{
        private val TAG = CurrencyDataWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        try {
            if(networkHelper.isNetworkConnected()){
                Log.d(TAG,"Network is available getting server data api ${BuildConfig.API_KEY}")
                val serverResponse = currencyApi.getData(
                    BuildConfig.API_KEY,
                    "USD",
                    "1"
                )
                if(serverResponse.isSuccessful){
                    //appDatabase.currencyDao().clearAll()
                    //insertIntoDb(appDatabase,processServerData(serverResponse.body()!!))
                    val data = Utill.processServerData(serverResponse.body()!!)
                    Log.d(TAG,"Networking getting data isSuccessful data size ${data.size}")
                    if(data.size > 0){
                        appDatabase.currencyDao().clearAll()
                        appDatabase.currencyDao().insertAll(data)
                    }
                    //currencyMutableLiveData.postValue(Resource.success("Response received",processData(serverResponse.body()!!)))
                    return Result.success()
                }else{
                    Log.d(TAG,"Networking getting data not Successful")
                    //currencyMutableLiveData.postValue(Resource.error("Error",null))
                    return Result.failure()
                }
            }else{
                return Result.failure()
            }
        }catch (exp: Throwable){
            Log.d(TAG,"${exp.cause} msg ${exp.message}")
            return Result.failure()
        }
    }

/*
    private fun processServerData(response: CurrencyResponse): List<CurrencyEntity> {
        Log.d(TAG,"Networking getImages() Processing data response ${response.quotes?.size}")
        val list = mutableListOf<CurrencyEntity>()
        response.quotes?.let {
            var id = 0;
            for (data in it) {
                Log.d(TAG,"processServerData response key ${data.key} value ${data.value}")
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
            }
        }
        return list
    }
*/

    private fun insertIntoDb(appDatabase: LocalDatabase,data: List<CurrencyEntity>){
        CoroutineScope(Dispatchers.Main).launch {
            data.apply {
                val insertionProcessDone = withContext(Dispatchers.IO){
                    try{
                        Log.d(TAG,"Worker insertIntoDb() insertingData ${data.size}")
                        appDatabase.currencyDao().insertAll(data)
                        true
                    }catch (exp: Exception){
                        false
                    }
                }
                if(insertionProcessDone){
                    Log.d(TAG,"All data inserted")
                }
            }
        }
    }
}