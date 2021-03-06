package com.sadikul.currencyconverter.data.local

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
}