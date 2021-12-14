package com.pactice.hild_mvvm_room.dada.api
import com.sadikul.currencyconverter.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("live")
    suspend fun getData(
        @Query("access_key") access_key: String,
        @Query(" source") source: String,
        @Query(" format") value: String
    ): Response<CurrencyResponse>

}