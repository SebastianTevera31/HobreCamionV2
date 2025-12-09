package com.rfz.appflotal.data.network.messaging

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessagingWorker @Inject constructor(
    @ApplicationContext context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d(TAG, "Performing long running task in scheduled job")
        // (developer): add long running task here.
        return Result.success()
    }

    companion object {
        private const val TAG = "MessagingWorker"
    }
}