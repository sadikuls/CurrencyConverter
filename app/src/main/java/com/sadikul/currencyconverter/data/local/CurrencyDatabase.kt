package com.sadikul.currencyconverter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import com.sadikul.currencyconverter.utils.Constants.DATABASE_NAME

@Database(entities = [CurrencyEntity::class], version = 1)
abstract class CurrencyDatabase: RoomDatabase(){
    companion object{
        private var appDb: CurrencyDatabase? = null
        fun getInstance(context: Context): CurrencyDatabase{
            if(appDb == null){
                appDb = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .addCallback(object : Callback(){
                    }).build()
            }
            return appDb!!
        }
    }

    abstract fun currencyDao() : CurrencyDao
}