package com.example.fitnesspaw.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitnesspaw.R

class WalkReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channel = NotificationChannel(
            "walk_channel",
            "Walk Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(
            applicationContext,
            "walk_channel"
        )
            .setContentTitle("FitnessPaw Walk 🐾")
            .setContentText("Let's hit our step goal today! Time for a short walk.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        manager.notify(3, notification)

        return Result.success()
    }
}
