package com.example.currencyconverter.data

import com.example.currencyconverter.data.local.RatesCache
import com.example.currencyconverter.data.remote.CurrencyApi
import com.example.currencyconverter.domain.model.ConversionRate
import com.example.currencyconverter.domain.model.Currency
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi,
    private val cache: RatesCache,
) : CurrencyRepository {

    private val clock: Clock = Clock.systemUTC()

    override suspend fun getCurrencies(): List<Currency> {
        cache.getCurrencies().takeIf { it.isNotEmpty() }?.let { return it }
        val fresh = api.getCurrencies()
            .map { (code, name) -> Currency(code, name) }
            .sortedBy { it.code }
        cache.saveCurrencies(fresh)
        return fresh
    }

    override suspend fun getRate(base: String, target: String): Result<ConversionRate> = runCatching {
        val dto = api.getLatestRate(base = base, symbols = target)
        val rate = dto.rates[target]
            ?: error("Frankfurter returned no rate for $target")
        ConversionRate(
            base = base,
            target = target,
            rate = rate,
            fetchedAt = clock.instant(),
        ).also { cache.saveLastRate(it) }
    }

    override suspend fun lastSelection(): Pair<String, String>? = cache.getSelection()

    override suspend fun saveSelection(source: String, target: String) {
        cache.saveSelection(source, target)
    }
}
