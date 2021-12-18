package com.sadikul.currencyconverter.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.sadikul.currencyconverter.data.local.entity.CurrencyEntity
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class LocalDatabaseTest{

    private lateinit var db: CurrencyDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    public fun setUp(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadSpend() = runBlockingTest {
        val currencyItem = CurrencyEntity(0,"usd",1.00,3245533)
        db.currencyDao().insert(item = currencyItem)
        val currencies = db.currencyDao().getAll()
        assertThat(currencies.contains(currencyItem)).isTrue()

    }
}