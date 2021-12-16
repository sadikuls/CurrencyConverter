package com.sadikul.currencyconverter.data.local

import android.util.Log
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.data.repository.CurrencyRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class LocalDataManager @Inject constructor(private val appDatabase: LocalDatabase){
    companion object{
        private val TAG = LocalDataManager::class.java.simpleName
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
                    Log.d(TAG,"All data inserted from LocalDataBase")
                }
            }
        }
    }

    private fun getAllFromDb(data: List<CurrencyEntity>){
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
                    Log.d(TAG,"All data inserted from LocalDataBase")
                }
            }
        }
    }
}