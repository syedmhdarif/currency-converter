package com.example.currencyconverter.domain.model

import java.time.Instant

data class ConversionRate(
    val base: String,
    val target: String,
    val rate: Double,
    val fetchedAt: Instant,
)
