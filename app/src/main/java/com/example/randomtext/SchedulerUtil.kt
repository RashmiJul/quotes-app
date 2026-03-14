package com.example.randomtext

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object SchedulerUtil {

    private const val WORK_NAME = "daily_quote_notification"

    fun schedule(context: Context) {
        val prefs = PrefsManager(context)
        if (!prefs.isEnabled) {
            cancel(context)
            return
        }

        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(prefs.hour, prefs.minute)
        var targetDateTime = now.toLocalDate().atTime(targetTime)

        // If the target time has already passed today, schedule for tomorrow
        if (targetDateTime.isBefore(now) || targetDateTime.isEqual(now)) {
            targetDateTime = targetDateTime.plusDays(1)
        }

        val delay = Duration.between(now, targetDateTime).toMillis()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
