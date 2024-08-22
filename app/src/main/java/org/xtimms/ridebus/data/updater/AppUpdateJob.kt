package org.xtimms.ridebus.data.updater

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import logcat.LogPriority
import org.xtimms.ridebus.BuildConfig
import org.xtimms.ridebus.util.system.logcat
import java.util.concurrent.TimeUnit

class AppUpdateJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = coroutineScope {
        try {
            AppUpdateChecker().checkForUpdate(context)
            Result.success()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "UpdateChecker"

        fun setupTask(context: Context) {
            if (!BuildConfig.INCLUDE_UPDATER) {
                cancelTask(context)
                return
            }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<AppUpdateJob>(
                7,
                TimeUnit.DAYS,
                3,
                TimeUnit.HOURS
            )
                .addTag(TAG)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, request)
        }

        fun cancelTask(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
        }
    }
}
