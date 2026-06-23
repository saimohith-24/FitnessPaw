package com.example.fitnesspaw.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitnesspaw.R

class WaterReminderWorker(

    context: Context,

    workerParams: WorkerParameters

) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel = NotificationChannel(
            "water_channel",
            "Water Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(
            applicationContext,
            "water_channel"
        )

            .setContentTitle("FitnessPaw 💧")

            .setContentText("Time to drink water!")

            .setSmallIcon(R.drawable.ic_launcher_foreground)

            .build()

        manager.notify(1, notification)

        return Result.success()
    }
}