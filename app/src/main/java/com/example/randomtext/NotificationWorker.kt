package com.example.randomtext

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val quote = QuoteRepository(applicationContext).getRandomQuote()
        NotificationHelper.showNotification(applicationContext, quote)

        // Re-schedule for the next day
        SchedulerUtil.schedule(applicationContext)

        return Result.success()
    }
}
