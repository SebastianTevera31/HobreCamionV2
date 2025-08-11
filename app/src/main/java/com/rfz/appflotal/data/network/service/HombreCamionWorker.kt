package com.rfz.appflotal.data.network.service

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.jvm.java

class HombreCamionWorker(ctx: Context, workerParams: WorkerParameters) : Worker(ctx, workerParams) {
    override fun doWork(): Result {
        return try {
            if (!isServiceRunning(applicationContext, HombreCamionService::class.java)) {
                if (hasAllRequiredPermissions(applicationContext)) {
                    val intent = Intent(applicationContext, HombreCamionService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(intent)
                    } else {
                        applicationContext.startService(intent)
                    }
                    Log.d("HCWorker", "Servicio Corriendo")
                    Result.success()
                } else {
                    // PERMISOS DENEGADOS
                    Log.d("HCWorker", "Permisos no concedidos")
                    Result.retry()
                }
            } else {
                // PERMISO CORRIENDO
                Log.d("HCWorker", "Servicio Corriendo")
                Result.success()
            }
        } catch (e: Exception) {
            Log.d("HCWorker", "${e.message}")
            Result.retry()
        }
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == serviceClass.name
        }
    }

    private fun hasAllRequiredPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.FOREGROUND_SERVICE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        } && if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        } && if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}