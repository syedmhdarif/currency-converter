package com.example.currencyconverter.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.currencyconverter.data.local.dto.CachedRateDto
import com.example.currencyconverter.domain.model.ConversionRate
import com.example.currencyconverter.domain.model.Currency
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ratesDataStore by preferencesDataStore(name = "rates_cache")

@Singleton
class RatesCache @Inject constructor(
    @ApplicationContext context: Context,
    private val json: Json,
) {
    private val dataStore = context.ratesDataStore

    suspend fun getCurrencies(): List<Currency> {
        val raw = dataStore.data.first()[CURRENCIES_KEY] ?: return emptyList()
        return runCatching {
            json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), raw)
                .map { (code, name) -> Currency(code, name) }
        }.getOrDefault(emptyList())
    }

    suspend fun saveCurrencies(currencies: List<Currency>) {
        val map = currencies.associate { it.code to it.name }
        val raw = json.encodeToString(MapSerializer(String.serializer(), String.serializer()), map)
        dataStore.edit { it[CURRENCIES_KEY] = raw }
    }

    suspend fun getLastRate(base: String, target: String): ConversionRate? {
        val raw = dataStore.data.first()[LAST_RATE_KEY] ?: return null
        return runCatching {
            val dto = json.decodeFromString(CachedRateDto.serializer(), raw)
            if (dto.base == base && dto.target == target) dto.toDomain() else null
        }.getOrNull()
    }

    suspend fun saveLastRate(rate: ConversionRate) {
        val raw = json.encodeToString(CachedRateDto.serializer(), CachedRateDto.fromDomain(rate))
        dataStore.edit { it[LAST_RATE_KEY] = raw }
    }

    suspend fun getSelection(): Pair<String, String>? {
        val prefs = dataStore.data.first()
        val source = prefs[SOURCE_KEY] ?: return null
        val target = prefs[TARGET_KEY] ?: return null
        return source to target
    }

    suspend fun saveSelection(source: String, target: String) {
        dataStore.edit {
            it[SOURCE_KEY] = source
            it[TARGET_KEY] = target
        }
    }

    private companion object {
        val CURRENCIES_KEY = stringPreferencesKey("currencies_json")
        val LAST_RATE_KEY = stringPreferencesKey("last_rate_json")
        val SOURCE_KEY = stringPreferencesKey("source_code")
        val TARGET_KEY = stringPreferencesKey("target_code")
    }
}
