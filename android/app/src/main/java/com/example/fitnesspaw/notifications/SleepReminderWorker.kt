package com.example.fitnesspaw.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitnesspaw.R

class SleepReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channel = NotificationChannel(
            "sleep_channel",
            "Sleep Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(
            applicationContext,
            "sleep_channel"
        )
            .setContentTitle("FitnessPaw Bedtime 😴")
            .setContentText("Wind down and get ready for a good night's rest to keep your pet happy!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        manager.notify(4, notification)

        return Result.success()
    }
}
