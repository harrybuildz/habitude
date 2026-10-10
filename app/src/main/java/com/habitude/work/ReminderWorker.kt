package com.habitude.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitude.MainActivity
import com.habitude.R
import com.habitude.data.HabitRepository
import com.habitude.domain.DateProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/** Posts a "don't break your streaks" notification listing habits still unchecked today. */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitRepository: HabitRepository,
    private val dateProvider: DateProvider,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val today = dateProvider.todayEpochDay()
        val unchecked = habitRepository.observeHabitsWithCompletions().first()
            .filter { item -> item.completions.none { it.dateEpochDay == today } }

        if (unchecked.isEmpty() || !canPostNotifications()) {
            return Result.success()
        }

        postNotification(unchecked.map { "${it.habit.emoji} ${it.habit.name}" })
        return Result.success()
    }

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    private fun postNotification(habitLines: List<String>) {
        val context = applicationContext
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.reminder_channel_description)
            },
        )

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.reminder_notification_title))
            .setContentText(habitLines.joinToString(", "))
            .setStyle(NotificationCompat.BigTextStyle().bigText(habitLines.joinToString("\n")))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "reminders"
        const val NOTIFICATION_ID = 1
    }
}
