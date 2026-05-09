package com.example.currencyconverter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LatestRatesDto(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
)
