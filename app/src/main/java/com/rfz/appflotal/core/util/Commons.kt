package com.rfz.appflotal.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.TimeZoneFormat
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Commons {
    private const val TAG = "CameraXCompose"
    fun showLog(log: String) {
        Log.d(TAG, log)
    }

    val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    fun allPermissionsGranted(ctx: Context) =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(ctx, it) ==
                    PackageManager.PERMISSION_GRANTED
        }

    fun validateBluetoothConnectivity(quality: BluetoothSignalQuality): Boolean {
        return quality in listOf(BluetoothSignalQuality.Excelente, BluetoothSignalQuality.Aceptable)
    }


    fun getCurrentDate(
        date: Date = Date(),
        pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    ): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun convertDate(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(date)!!
        val outDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return outDate.format(date)
    }
}