package com.sadikul.currencyconverter.data.local.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "currency")
data class CurrencyEntity(
    @Nullable
    @ColumnInfo(name = "currency_name")
    val currency_name: String? = null,

    @Nullable
    @ColumnInfo(name = "currency_value")
    val currency_value: Int? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Int
) : Parcelable