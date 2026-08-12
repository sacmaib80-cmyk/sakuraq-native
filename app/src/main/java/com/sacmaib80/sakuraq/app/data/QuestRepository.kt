package com.sacmaib80.sakuraq.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sakuraq")

class QuestRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val stateKey = stringPreferencesKey("state")

    val data: Flow<AppData> = context.dataStore.data.map { prefs ->
        val raw = prefs[stateKey]
        if (raw.isNullOrBlank()) AppData()
        else runCatching { json.decodeFromString<AppData>(raw) }.getOrDefault(AppData())
    }

    suspend fun save(data: AppData) {
        context.dataStore.edit { prefs ->
            prefs[stateKey] = json.encodeToString(AppData.serializer(), data)
        }
    }
}
