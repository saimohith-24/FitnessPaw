package com.example.fitnesspaw.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleReminders() {
        val workManager = WorkManager.getInstance(context)

        // Water reminder every 2 hours
        val waterRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(2, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            waterRequest
        )

        // Walk reminder every 4 hours
        val walkRequest = PeriodicWorkRequestBuilder<WalkReminderWorker>(4, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "walk_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            walkRequest
        )

        // Sleep reminder every 8 hours
        val sleepRequest = PeriodicWorkRequestBuilder<SleepReminderWorker>(8, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "sleep_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            sleepRequest
        )
    }

    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
