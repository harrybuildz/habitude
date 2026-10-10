package com.habitude.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            darkTheme = preferences[DARK_THEME],
            reminderEnabled = preferences[REMINDER_ENABLED] ?: false,
            reminderHour = preferences[REMINDER_HOUR] ?: UserSettings.DEFAULT_REMINDER_HOUR,
        )
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[DARK_THEME] = enabled }
    }

    override suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[REMINDER_ENABLED] = enabled }
    }

    override suspend fun setReminderHour(hour: Int) {
        dataStore.edit { it[REMINDER_HOUR] = hour.coerceIn(0, 23) }
    }

    private companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    }
}
