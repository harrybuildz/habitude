package com.habitude.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {

    override fun scheduleDaily(hour: Int) {
        val now = LocalDateTime.now()
        var nextRun = now.toLocalDate().atTime(hour, 0)
        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusDays(1)
        }

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(Duration.ofDays(1))
            .setInitialDelay(Duration.between(now, nextRun))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_reminder"
    }
}
