package com.habitude.work

/** Boundary for reminder scheduling so ViewModels never touch WorkManager directly. */
interface ReminderScheduler {
    fun scheduleDaily(hour: Int)
    fun cancel()
}
