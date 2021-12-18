package com.sadikul.currencyconverter.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sadikul.currencyconverter.data.repository.CurrencyRepo
import com.sadikul.currencyconverter.utils.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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
        var result = Result.failure()
        repository.getDataFromServer("USD","1") { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "remote-data Data successfully got from server")
                    result = Result.success()
                }

                Status.ERROR -> {
                    Log.d(TAG, "remote-data failed to get data from server")
                    result = Result.failure()
                }
            }
        }
        return result
    }
}