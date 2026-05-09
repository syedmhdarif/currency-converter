package com.example.currencyconverter.data.remote

import com.example.currencyconverter.data.remote.dto.LatestRatesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("currencies")
    suspend fun getCurrencies(): Map<String, String>

    @GET("latest")
    suspend fun getLatestRate(
        @Query("base") base: String,
        @Query("symbols") symbols: String,
    ): LatestRatesDto
}
