package com.rfz.appflotal.core.util

import android.content.Context

object AppVersionUtil {

    fun getVersionName(context: Context): String? {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName
    }

    fun getVersionCode(context: Context): Long {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.longVersionCode
    }
}