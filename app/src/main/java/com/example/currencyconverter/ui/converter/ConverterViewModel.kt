package com.example.currencyconverter.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ConverterUiState())
    val state: StateFlow<ConverterUiState> = _state.asStateFlow()

    private var rateJob: Job? = null

    init {
        viewModelScope.launch {
            val (source, target) = repository.lastSelection() ?: ("USD" to "MYR")
            _state.update { it.copy(sourceCode = source, targetCode = target) }
            loadCurrencies()
            fetchRate()
        }
    }

    fun onAmountChange(input: String) {
        val sanitized = input.filter { it.isDigit() || it == '.' }
        _state.update {
            it.copy(
                amountInput = sanitized,
                convertedAmount = computeConverted(sanitized, it.rate),
            )
        }
    }

    fun onSourceSelected(code: String) {
        if (code == _state.value.sourceCode) return
        _state.update { it.copy(sourceCode = code) }
        persistSelection()
        fetchRate()
    }

    fun onTargetSelected(code: String) {
        if (code == _state.value.targetCode) return
        _state.update { it.copy(targetCode = code) }
        persistSelection()
        fetchRate()
    }

    fun onSwap() {
        _state.update { it.copy(sourceCode = it.targetCode, targetCode = it.sourceCode) }
        persistSelection()
        fetchRate()
    }

    fun onRetry() = fetchRate()

    private fun loadCurrencies() = viewModelScope.launch {
        runCatching { repository.getCurrencies() }
            .onSuccess { currencies ->
                _state.update { it.copy(availableCurrencies = currencies) }
            }
    }

    private fun persistSelection() = viewModelScope.launch {
        val current = _state.value
        repository.saveSelection(current.sourceCode, current.targetCode)
    }

    private fun fetchRate() {
        rateJob?.cancel()
        rateJob = viewModelScope.launch {
            val current = _state.value
            _state.update { it.copy(phase = ConverterUiState.Phase.Loading) }
            repository.getRate(current.sourceCode, current.targetCode)
                .onSuccess { conversion ->
                    _state.update {
                        it.copy(
                            rate = conversion.rate,
                            lastUpdated = conversion.fetchedAt,
                            convertedAmount = computeConverted(it.amountInput, conversion.rate),
                            phase = ConverterUiState.Phase.Idle,
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            phase = ConverterUiState.Phase.Error(
                                throwable.message ?: "Failed to fetch rate",
                            ),
                        )
                    }
                }
        }
    }

    private fun computeConverted(input: String, rate: Double?): String? {
        if (rate == null) return null
        val amount = input.toDoubleOrNull() ?: return null
        return String.format(Locale.US, "%.4f", amount * rate)
    }
}
