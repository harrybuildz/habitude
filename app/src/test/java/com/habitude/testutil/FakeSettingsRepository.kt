package com.habitude.testutil

import com.habitude.data.SettingsRepository
import com.habitude.data.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepository(
    initial: UserSettings = UserSettings(),
) : SettingsRepository {

    private val settingsFlow = MutableStateFlow(initial)

    override val settings: Flow<UserSettings> = settingsFlow.asStateFlow()

    val current: UserSettings get() = settingsFlow.value

    override suspend fun setDarkTheme(enabled: Boolean) {
        settingsFlow.update { it.copy(darkTheme = enabled) }
    }

    override suspend fun setReminderEnabled(enabled: Boolean) {
        settingsFlow.update { it.copy(reminderEnabled = enabled) }
    }

    override suspend fun setReminderHour(hour: Int) {
        settingsFlow.update { it.copy(reminderHour = hour) }
    }
}
