package com.sadikul.currencyconverter.data.local.dao

import androidx.room.*
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity

@Dao
interface CurrencyDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CurrencyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<CurrencyEntity>)

    @Query("select * from currency")
    suspend fun getAll() : List<CurrencyEntity>

    @Query("delete from currency")
    suspend fun clearAll()

/*    @Query("select * from currency where currency_to like :value")
    suspend fun getByCurrency(value: String)*/

/*
    @Query("select currencyTo from currency")
    suspend fun getAllCurrency()*/
}