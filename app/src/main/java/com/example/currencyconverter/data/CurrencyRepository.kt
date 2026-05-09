package com.example.currencyconverter.data

import com.example.currencyconverter.domain.model.ConversionRate
import com.example.currencyconverter.domain.model.Currency

interface CurrencyRepository {
    suspend fun getCurrencies(): List<Currency>
    suspend fun getRate(base: String, target: String): Result<ConversionRate>
    suspend fun lastSelection(): Pair<String, String>?
    suspend fun saveSelection(source: String, target: String)
}
