package com.pactice.hild_mvvm_room.dada.api
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService): ApiHelper{

    override suspend fun getData(
        access_key: String,
        source: String,
        value: String
    ): Response<CurrencyResponse> = apiService.getData(access_key, source, value);

}