package com.example.fitnesspaw.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitnesspaw.R

class TaskReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel = NotificationChannel(
            "task_channel",
            "Task Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        val taskTitle = inputData.getString("task_title") ?: "Complete your scheduled task!"
        val taskId = inputData.getString("task_id") ?: ""
        val notificationId = if (taskId.isNotEmpty()) taskId.hashCode() else 2

        val notification = NotificationCompat.Builder(
            applicationContext,
            "task_channel"
        )
            .setContentTitle("FitnessPaw Reminder 🐾")
            .setContentText(taskTitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        manager.notify(notificationId, notification)

        return Result.success()
    }
}