package com.example.currencyconverter.ui.converter

import com.example.currencyconverter.domain.model.Currency
import java.time.Instant

data class ConverterUiState(
    val amountInput: String = "1",
    val sourceCode: String = "USD",
    val targetCode: String = "MYR",
    val rate: Double? = null,
    val convertedAmount: String? = null,
    val lastUpdated: Instant? = null,
    val availableCurrencies: List<Currency> = emptyList(),
    val phase: Phase = Phase.Idle,
) {
    sealed interface Phase {
        data object Idle : Phase
        data object Loading : Phase
        data class Error(val message: String) : Phase
    }
}
