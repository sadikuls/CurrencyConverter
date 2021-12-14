package com.pactice.hild_mvvm_room.dada.api
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun getData(access_key: String, source: String, format: String): Response<CurrencyResponse>
}