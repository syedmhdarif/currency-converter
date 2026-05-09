package com.example.currencyconverter.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardOptions
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.ui.theme.AppTheme
import com.example.currencyconverter.ui.theme.LocalSpacing

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ConverterScreenContent(
        state = state,
        onAmountChange = viewModel::onAmountChange,
        onSourceSelected = viewModel::onSourceSelected,
        onTargetSelected = viewModel::onTargetSelected,
        onSwap = viewModel::onSwap,
        onRetry = viewModel::onRetry,
    )
}

@Composable
private fun ConverterScreenContent(
    state: ConverterUiState,
    onAmountChange: (String) -> Unit,
    onSourceSelected: (String) -> Unit,
    onTargetSelected: (String) -> Unit,
    onSwap: () -> Unit,
    onRetry: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.md, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(
                text = "Currency Converter",
                style = MaterialTheme.typography.headlineMedium,
            )

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                CurrencyDropdown(
                    label = "From",
                    selected = state.sourceCode,
                    currencies = state.availableCurrencies,
                    onSelected = onSourceSelected,
                    modifier = Modifier.weight(1f),
                )
                FilledTonalIconButton(onClick = onSwap) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Swap currencies")
                }
                CurrencyDropdown(
                    label = "To",
                    selected = state.targetCode,
                    currencies = state.availableCurrencies,
                    onSelected = onTargetSelected,
                    modifier = Modifier.weight(1f),
                )
            }

            ResultCard(state = state, onRetry = onRetry)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    label: String,
    selected: String,
    currencies: List<Currency>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text("${currency.code} — ${currency.name}") },
                    onClick = {
                        onSelected(currency.code)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ResultCard(state: ConverterUiState, onRetry: () -> Unit) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            when (val phase = state.phase) {
                ConverterUiState.Phase.Loading -> {
                    Text(
                        text = "Fetching latest rate…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is ConverterUiState.Phase.Error -> {
                    Text(
                        text = phase.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(onClick = onRetry) { Text("Retry") }
                }
                ConverterUiState.Phase.Idle -> {
                    Text(
                        text = "${state.amountInput.ifEmpty { "0" }} ${state.sourceCode}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "= ${state.convertedAmount ?: "—"} ${state.targetCode}",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    state.rate?.let { rate ->
                        Text(
                            text = "1 ${state.sourceCode} = $rate ${state.targetCode}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConverterScreenPreview() {
    AppTheme(dynamicColor = false) {
        Surface {
            ConverterScreenContent(
                state = ConverterUiState(
                    amountInput = "100",
                    sourceCode = "USD",
                    targetCode = "MYR",
                    rate = 4.71,
                    convertedAmount = "471.00",
                    availableCurrencies = listOf(
                        Currency("USD", "US Dollar"),
                        Currency("MYR", "Malaysian Ringgit"),
                    ),
                ),
                onAmountChange = {},
                onSourceSelected = {},
                onTargetSelected = {},
                onSwap = {},
                onRetry = {},
            )
        }
    }
}
