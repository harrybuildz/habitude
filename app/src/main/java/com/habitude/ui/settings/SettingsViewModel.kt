package com.habitude.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.data.SettingsRepository
import com.habitude.work.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val darkTheme: Boolean? = null,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.settings
        .map { settings ->
            SettingsUiState(
                darkTheme = settings.darkTheme,
                reminderEnabled = settings.reminderEnabled,
                reminderHour = settings.reminderHour,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setReminderEnabled(enabled)
            if (enabled) {
                reminderScheduler.scheduleDaily(settingsRepository.settings.first().reminderHour)
            } else {
                reminderScheduler.cancel()
            }
        }
    }

    fun setReminderHour(hour: Int) {
        viewModelScope.launch {
            settingsRepository.setReminderHour(hour)
            if (settingsRepository.settings.first().reminderEnabled) {
                reminderScheduler.scheduleDaily(hour)
            }
        }
    }
}
