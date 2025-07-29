package com.rfz.appflotal.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

object Commons {
    private const val TAG = "CameraXCompose"
    fun showLog(log: String) {
        Log.d(com.rfz.appflotal.core.util.Commons.TAG, log)
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
        com.rfz.appflotal.core.util.Commons.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(ctx, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
}