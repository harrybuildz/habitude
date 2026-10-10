package com.habitude.data

data class UserSettings(
    /** `null` means "follow the system theme" (the user has never toggled it). */
    val darkTheme: Boolean? = null,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = DEFAULT_REMINDER_HOUR,
) {
    companion object {
        const val DEFAULT_REMINDER_HOUR = 20
    }
}
