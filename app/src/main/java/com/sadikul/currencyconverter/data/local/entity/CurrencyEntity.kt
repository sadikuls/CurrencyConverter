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
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    val id: Int,

    @Nullable
    @ColumnInfo(name = "currency")
    val currency: String? = null,

    @Nullable
    @ColumnInfo(name = "value")
    val value: Double? = null,

    @Nullable
    @ColumnInfo(name = "time")
    val time: Long? = null,

) : Parcelable