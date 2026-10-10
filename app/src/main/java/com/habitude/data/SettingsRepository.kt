package com.habitude.data

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setReminderEnabled(enabled: Boolean)
    suspend fun setReminderHour(hour: Int)
}
