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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject
@HiltWorker
class ServerDataReceiver @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val networkHelper: NetworkHelper,
    private val currencyApi: CurrencyApi,
    private val appDatabase: LocalDatabase
) : CoroutineWorker(appContext, workerParams) {

    companion object{
        private val TAG = ServerDataReceiver::class.java.simpleName
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
                    Log.d(TAG,"Networking getting data isSuccessful")
                    insertIntoDb(processData(serverResponse.body()!!))
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

    private fun processData(response: CurrencyResponse): List<CurrencyEntity> {
        Log.d(TAG,"Networking getImages() Processing data")
        val list = mutableListOf<CurrencyEntity>()
        response.quotes?.let {
            var id = 0;
            for (data in it) {
                id++;
                data.apply {
                    val item = CurrencyEntity(
                        id,
                        data.key,
                        data.value,
                    )
                    list.add(item)
                }
            }
        }
        return list
    }

    private fun insertIntoDb(data: List<CurrencyEntity>){
        CoroutineScope(Dispatchers.Main).launch {
            data.apply {
                val insertionProcessDone = withContext(Dispatchers.IO){
                    try{
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