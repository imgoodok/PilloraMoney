package com.example.pilloramoney.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val TAG = "NotificationScheduler"
    private const val REMINDER_WORK_TAG = "daily_reminder_notification"
    private const val SAVINGS_WORK_TAG = "daily_savings_notification"
    private const val REMINDER_WORK_ID = "daily_reminder_9am"
    private const val SAVINGS_WORK_ID = "daily_savings_10am"

    fun scheduleDailyNotifications(context: Context) {
        Log.d(TAG, "Iniciando agendamento de notificações diárias")
        scheduleReminderNotification(context)  // 9:00
        scheduleSavingsNotification(context)   // 10:00
    }

    private fun scheduleReminderNotification(context: Context) {
        val delayMinutes = calculateDelayUntilTime(9, 0)
        Log.d(TAG, "Agendando lembrete das 9h. Atraso inicial: $delayMinutes minutos")
        
        val reminderWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(
                Data.Builder()
                    .putString("type", "reminder")
                    .build()
            )
            .addTag(REMINDER_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REMINDER_WORK_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }

    private fun scheduleSavingsNotification(context: Context) {
        val delayMinutes = calculateDelayUntilTime(10, 0)
        Log.d(TAG, "Agendando economia das 10h. Atraso inicial: $delayMinutes minutos")
        
        val savingsWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(
                Data.Builder()
                    .putString("type", "savings")
                    .build()
            )
            .addTag(SAVINGS_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SAVINGS_WORK_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            savingsWork
        )
    }

    fun runTestNotification(context: Context) {
        Log.d(TAG, "Disparando notificação de teste imediata")
        val testWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(
                Data.Builder()
                    .putString("type", "reminder")
                    .build()
            )
            .build()
        
        WorkManager.getInstance(context).enqueue(testWork)
    }

    private fun calculateDelayUntilTime(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Se o horário já passou hoje, agenda para amanhã
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delayMillis = target.timeInMillis - now.timeInMillis
        return delayMillis / 60000 // Converte para minutos
    }

    fun cancelDailyNotifications(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelAllWorkByTag(REMINDER_WORK_TAG)
            cancelAllWorkByTag(SAVINGS_WORK_TAG)
        }
    }
}
