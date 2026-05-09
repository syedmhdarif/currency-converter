package com.example.currencyconverter.data

import com.example.currencyconverter.data.local.RatesCache
import com.example.currencyconverter.data.remote.CurrencyApi
import com.example.currencyconverter.data.remote.dto.LatestRatesDto
import com.example.currencyconverter.domain.model.Currency
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyRepositoryImplTest {

    private val api: CurrencyApi = mockk()
    private val cache: RatesCache = mockk(relaxUnitFun = true)
    private val repo = CurrencyRepositoryImpl(api, cache)

    @Test
    fun `getCurrencies returns cached list without hitting api`() = runTest {
        coEvery { cache.getCurrencies() } returns listOf(Currency("USD", "US Dollar"))

        val result = repo.getCurrencies()

        assertEquals(listOf(Currency("USD", "US Dollar")), result)
        coVerify(exactly = 0) { api.getCurrencies() }
    }

    @Test
    fun `getCurrencies fetches sorted list and caches when cache is empty`() = runTest {
        coEvery { cache.getCurrencies() } returns emptyList()
        coEvery { api.getCurrencies() } returns mapOf("USD" to "US Dollar", "EUR" to "Euro")

        val result = repo.getCurrencies()

        assertEquals(listOf("EUR", "USD"), result.map { it.code })
        coVerify { cache.saveCurrencies(any()) }
    }

    @Test
    fun `getRate caches and returns success on api success`() = runTest {
        coEvery { api.getLatestRate("USD", "MYR") } returns LatestRatesDto(
            amount = 1.0,
            base = "USD",
            date = "2026-05-08",
            rates = mapOf("MYR" to 4.71),
        )

        val result = repo.getRate("USD", "MYR")

        assertTrue(result.isSuccess)
        assertEquals(4.71, result.getOrNull()?.rate ?: 0.0, 0.001)
        coVerify { cache.saveLastRate(any()) }
    }

    @Test
    fun `getRate returns failure and skips caching when api throws`() = runTest {
        coEvery { api.getLatestRate(any(), any()) } throws RuntimeException("network down")

        val result = repo.getRate("USD", "MYR")

        assertTrue(result.isFailure)
        assertEquals("network down", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { cache.saveLastRate(any()) }
    }

    @Test
    fun `getRate fails when target currency missing from response`() = runTest {
        coEvery { api.getLatestRate("USD", "XYZ") } returns LatestRatesDto(
            amount = 1.0,
            base = "USD",
            date = "2026-05-08",
            rates = emptyMap(),
        )

        val result = repo.getRate("USD", "XYZ")

        assertTrue(result.isFailure)
    }
}
