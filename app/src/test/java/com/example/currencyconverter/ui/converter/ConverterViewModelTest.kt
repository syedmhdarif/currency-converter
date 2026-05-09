package com.example.currencyconverter.ui.converter

import com.example.currencyconverter.MainDispatcherRule
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.domain.model.ConversionRate
import com.example.currencyconverter.domain.model.Currency
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CurrencyRepository = mockk(relaxUnitFun = true)

    @Test
    fun `init loads saved selection, currencies, and fetches rate`() = runTest {
        coEvery { repository.lastSelection() } returns ("EUR" to "GBP")
        coEvery { repository.getCurrencies() } returns listOf(
            Currency("EUR", "Euro"),
            Currency("GBP", "British Pound"),
        )
        coEvery { repository.getRate("EUR", "GBP") } returns Result.success(
            ConversionRate("EUR", "GBP", 0.85, Instant.now())
        )

        val vm = ConverterViewModel(repository)
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals("EUR", state.sourceCode)
        assertEquals("GBP", state.targetCode)
        assertEquals(0.85, state.rate ?: 0.0, 0.001)
        assertEquals(2, state.availableCurrencies.size)
        assertTrue(state.phase is ConverterUiState.Phase.Idle)
    }

    @Test
    fun `init defaults to USD-MYR when no saved selection`() = runTest {
        coEvery { repository.lastSelection() } returns null
        coEvery { repository.getCurrencies() } returns emptyList()
        coEvery { repository.getRate("USD", "MYR") } returns Result.success(
            ConversionRate("USD", "MYR", 4.0, Instant.now())
        )

        val vm = ConverterViewModel(repository)
        advanceUntilIdle()

        assertEquals("USD", vm.state.value.sourceCode)
        assertEquals("MYR", vm.state.value.targetCode)
    }

    @Test
    fun `onAmountChange recomputes converted amount immediately`() = runTest {
        coEvery { repository.lastSelection() } returns null
        coEvery { repository.getCurrencies() } returns emptyList()
        coEvery { repository.getRate("USD", "MYR") } returns Result.success(
            ConversionRate("USD", "MYR", 4.0, Instant.now())
        )

        val vm = ConverterViewModel(repository)
        advanceUntilIdle()

        vm.onAmountChange("25")

        assertEquals("100.0000", vm.state.value.convertedAmount)
    }

    @Test
    fun `onAmountChange strips non-numeric characters`() = runTest {
        coEvery { repository.lastSelection() } returns null
        coEvery { repository.getCurrencies() } returns emptyList()
        coEvery { repository.getRate("USD", "MYR") } returns Result.success(
            ConversionRate("USD", "MYR", 4.0, Instant.now())
        )

        val vm = ConverterViewModel(repository)
        advanceUntilIdle()

        vm.onAmountChange("abc12.5xyz")

        assertEquals("12.5", vm.state.value.amountInput)
    }

    @Test
    fun `onSwap inverts source and target and refetches`() = runTest {
        coEvery { repository.lastSelection() } returns null
        coEvery { repository.getCurrencies() } returns emptyList()
        coEvery { repository.getRate("USD", "MYR") } returns Result.success(
            ConversionRate("USD", "MYR", 4.0, Instant.now())
        )
        coEvery { repository.getRate("MYR", "USD") } returns Result.success(
            ConversionRate("MYR", "USD", 0.25, Instant.now())
        )

        val vm = ConverterViewModel(repository)
        advanceUntilIdle()

        vm.onSwap()
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals("MYR", state.sourceCode)
        assertEquals("USD", state.targetCode)
        assertEquals(0.25, state.rate ?: 0.0, 0.001)
    }

    @Test
    fun `phase becomes Error when repository fails`() = runTest {
        coEvery { repository.lastSelection() } returns null
        coEvery { repository.getCurrencies() } returns emptyList()
        coEvery { repository.getRate("USD", "MYR") } returns Result.failure(
            RuntimeException("network down")
        )

        val vm = ConverterViewModel(repository)
        advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state.phase is ConverterUiState.Phase.Error)
        assertEquals("network down", (state.phase as ConverterUiState.Phase.Error).message)
    }
}
