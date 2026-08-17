package com.example.pilloramoney.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pilloramoney.MainActivity
import com.example.pilloramoney.R

object PilloraNotificationManager {
    private const val CHANNEL_ID = "pillora_money_alerts_v2"
    private const val CHANNEL_NAME = "Alertas Pillora Money"
    private const val CHANNEL_DESC = "Notificações de lembretes e dicas financeiras"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Como criar uma nova notificação:
     * Basta chamar esta função passando um ID único (para não sobrescrever outras),
     * o título e a mensagem desejada.
     */
    fun sendNotification(context: Context, id: Int, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }

    /**
     * Enviar notificação com deep link:
     * Permite que ao clicar na notificação, o app navegue para uma tela específica
     */
    fun sendNotificationWithDeepLink(
        context: Context,
        id: Int,
        title: String,
        message: String,
        deepLink: String
    ) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)).apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }

    /**
     * Como remover uma notificação:
     * Basta chamar esta função com o mesmo ID usado na criação.
     */
    fun cancelNotification(context: Context, id: Int) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }
}
