package com.example.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MyWorker(
    context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    private var timer: Timer = Timer(10000, 1000)
    private val notificationHelper = NotificationHelper(applicationContext)

    override suspend fun doWork(): Result {
        showNotification()
        try {
            runTimer()
        } catch (e: Exception) {
                timer.cancel()
                return Result.failure()
        }
        return Result.success()
    }

    private suspend fun showNotification() {
        setForeground(
            ForegroundInfo(
                NOTIFICATION_ID,
                notificationHelper.buildNotification(timer.currentTime.value)
            )
        )
        Log.d("TAG", "showNotification")
    }

    private suspend fun runTimer() {
        withContext(Dispatchers.Main) {
            timer.start()
            timer.currentTime.observeForever {
                notificationHelper.updateNotification(NOTIFICATION_ID, it)
            }

            while (!timer.timerIsFinished) {
                delay(100)
            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}