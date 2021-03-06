package com.sadikul.currencyconverter.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sadikul.currencyconverter.data.repository.CurrencyRepo
import com.sadikul.currencyconverter.utils.Constants.DEFAULT_CURRENCY_SOURCE
import com.sadikul.currencyconverter.utils.Constants.DEFAULT_CURRENCY_VALUE
import com.sadikul.currencyconverter.utils.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltWorker
class CurrencyDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CurrencyRepo
) : CoroutineWorker(appContext, workerParams) {

    companion object{
        private val TAG = CurrencyDataWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        repository.getDataFromServer(DEFAULT_CURRENCY_SOURCE, DEFAULT_CURRENCY_VALUE) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "remote-data Data successfully got from server")
                    response.data?.let {
                        if (it.size > 0) {
                            CoroutineScope(Dispatchers.Default).launch {
                                withContext(Dispatchers.IO) {
                                    Log.d(TAG, "remote-data inserting data to db")
                                    repository.clearDb()
                                    repository.insertToDb(it)
                                }
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    response.message?.let{
                        Log.d(TAG, "remote-data failed to get data from server, error ${it}")
                    }
                }
                else -> {
                    Log.d(TAG, "Something went wrong.")
                }
            }
        }
        return Result.success()
    }
}