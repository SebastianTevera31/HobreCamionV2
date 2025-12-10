package com.rfz.appflotal.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        return quality != BluetoothSignalQuality.Desconocida
    }

    fun isValidMacAddress(mac: String): Boolean {
        val macRegex = "^([0-9A-Fa-f]{2}[:\\-]){5}([0-9A-Fa-f]{2})$".toRegex()
        return mac.matches(macRegex)
    }

    fun getCurrentDate(
        date: Date = Date(),
        pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    ): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun convertDate(
        date: String,
        initialFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
        convertFormat: String = "dd/MM/yyyy HH:mm:ss"
    ): String {
        val sdf = SimpleDateFormat(initialFormat, Locale.getDefault())
        val date = sdf.parse(date)!!
        val outDate = SimpleDateFormat(convertFormat, Locale.getDefault())
        return outDate.format(date)
    }

    fun getDateObject(
        date: String,
        initialFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
    ): Date {
        val sdf = SimpleDateFormat(initialFormat, Locale.getDefault())
        return sdf.parse(date)!!
    }

    fun addOneDay(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_MONTH, 1)
        return cal.time
    }

    fun getBitmapFromDrawable(@DrawableRes image: Int, context: Context): Bitmap? {
        return BitmapFactory.decodeResource(context.resources, image)
    }

    fun getDateFromNotification(fecha: String, hora: String): ZonedDateTime? {
        val localDate = LocalDate.parse(fecha)
        val localHour = LocalTime.parse(hora.chunked(2).joinToString(":"))
        val horaFinal = LocalDateTime.of(localDate, localHour)
        val zonaLocal = ZoneId.systemDefault()
        return horaFinal.atZone(zonaLocal)
    }
}