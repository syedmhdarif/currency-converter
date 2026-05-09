package com.example.currencyconverter.ui.converter

data class ConverterUiState(
    val amountInput: String = "1",
    val sourceCode: String = "USD",
    val targetCode: String = "MYR",
    val convertedAmount: String? = null,
    val rate: Double? = null,
    val lastUpdated: String? = null,
    val phase: Phase = Phase.Idle,
) {
    sealed interface Phase {
        data object Idle : Phase
        data object Loading : Phase
        data class Error(val message: String) : Phase
    }
}
