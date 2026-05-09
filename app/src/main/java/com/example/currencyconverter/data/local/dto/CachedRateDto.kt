package com.example.currencyconverter.data.local.dto

import com.example.currencyconverter.domain.model.ConversionRate
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CachedRateDto(
    val base: String,
    val target: String,
    val rate: Double,
    val fetchedAtEpochMs: Long,
) {
    fun toDomain(): ConversionRate = ConversionRate(
        base = base,
        target = target,
        rate = rate,
        fetchedAt = Instant.ofEpochMilli(fetchedAtEpochMs),
    )

    companion object {
        fun fromDomain(rate: ConversionRate): CachedRateDto = CachedRateDto(
            base = rate.base,
            target = rate.target,
            rate = rate.rate,
            fetchedAtEpochMs = rate.fetchedAt.toEpochMilli(),
        )
    }
}
