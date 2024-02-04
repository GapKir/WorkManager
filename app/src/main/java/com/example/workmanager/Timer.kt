package com.example.workmanager

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Timer(
    private val millisInFuture: Long,
    private val countDownInterval: Long,
) {
    private var countDownTimer: CountDownTimer? = null

    private val _currentTime = MutableLiveData(FINISHED_TIME)
    val currentTime: LiveData<String> = _currentTime

    var timerIsFinished = false
    private var millisUntilFinished: Long = 0
    private var timerIsPaused = false

    fun start() {
        countDownTimer = if (timerIsPaused) {
            createCountDownTimer(millisUntilFinished)
        } else {
            createCountDownTimer(millisInFuture)
        }
        countDownTimer?.start()
    }

    fun cancel() {
        countDownTimer?.cancel()
    }

    private fun createCountDownTimer(millis: Long): CountDownTimer {
        return object : CountDownTimer(millis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                this@Timer.millisUntilFinished = millisUntilFinished
                updateTimer(millisUntilFinished / 1000)
                Log.d("TAG", "onTick")
            }

            override fun onFinish() {
                timerIsFinished = true
                _currentTime.value = FINISHED_TIME
                Log.d("TAG", "onFinish")
            }
        }
    }


    private fun updateTimer(secondsRemaining: Long) {
        val hours = secondsRemaining / 3600
        val minutes = (secondsRemaining % 3600) / 60
        val seconds = secondsRemaining % 60

        _currentTime.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    companion object {
        private const val FINISHED_TIME = "00:00:00"
    }
}