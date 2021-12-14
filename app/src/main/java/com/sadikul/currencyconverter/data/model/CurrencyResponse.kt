package com.sadikul.currencyconverter.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(

	@field:SerializedName("terms")
	val terms: String? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("privacy")
	val privacy: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: Int? = null,

	@field:SerializedName("quotes")
	val quotes: HashMap<String,Double>? = null
)