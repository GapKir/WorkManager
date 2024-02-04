package com.example.workmanager

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationHelper(
    private val context: Context
) {

    fun buildNotification(text: String?): Notification {
        return NotificationCompat.Builder(context, App.CHANNEL_ID)
            .setContentTitle(TITLE)
            .setSound(null)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    fun updateNotification(id: Int, text: String) {
        context.getSystemService(NotificationManager::class.java)
            .notify(id, buildNotification(text))
    }

    companion object{
        private const val TITLE = "Таймер"
    }
}