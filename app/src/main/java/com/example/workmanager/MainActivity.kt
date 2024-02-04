package com.example.workmanager

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(applicationContext)
    }

    private val currentWorkId: MutableLiveData<UUID?> = MutableLiveData(null)

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        findViewById<Button>(R.id.btn_start_timer).setOnClickListener {
            startTimer()
        }

        findViewById<Button>(R.id.btn_pause_timer).setOnClickListener {
            pauseTimer()
        }

        findViewById<Button>(R.id.btn_reset_timer).setOnClickListener {
            resetTimer()
        }
    }


    private fun observeViewModel() {
        currentWorkId.observe(this) { uuid ->
            uuid?.let {
                workManager.getWorkInfoByIdLiveData(it).observe(this) { workInfo ->
                    if (workInfo.state.isFinished) {
                        currentWorkId.value = null
                    }
                }
            }
        }
    }

    private fun startTimer() {
        if (currentWorkId.value == null) {
            workManager.enqueue(createWorkRequest())
        }
    }

    private fun pauseTimer() {
        if (currentWorkId.value != null) {
            ///
        }
    }

    private fun resetTimer() {
        currentWorkId.value?.let {
            workManager.cancelWorkById(it)
            Log.d("TAG", "resetTimer")
        }
    }

    private fun createWorkRequest(data: Data? = null): WorkRequest {
        val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        if (data != null) {
            workRequest.setInputData(data)
        }
        return workRequest.build().also { currentWorkId.value = it.id }
    }
}