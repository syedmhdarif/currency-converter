package com.example.currencyconverter.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.currencyconverter.ui.theme.AppTheme
import com.example.currencyconverter.ui.theme.LocalSpacing

@Composable
fun ConverterScreen(
    state: ConverterUiState = ConverterUiState(),
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
            Spacer(Modifier.height(spacing.sm))
            Text(
                text = "${state.amountInput} ${state.sourceCode}",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = "= ${state.convertedAmount ?: "—"} ${state.targetCode}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Compose smoke-test stub. Real UI lands in Step 11.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConverterScreenPreview() {
    AppTheme(dynamicColor = false) {
        Surface { ConverterScreen() }
    }
}
