package com.rfz.appflotal.core.util

import android.util.Log
import com.rfz.appflotal.BuildConfig

object AppLog {
    private const val ENABLE_LOGS = BuildConfig.BUILD_TYPE

    fun d(tag: String, message: String) {
        if (ENABLE_LOGS == "debug") Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        if (ENABLE_LOGS == "debug") Log.i(tag, message)
    }

    fun w(tag: String, message: String) {
        if (ENABLE_LOGS == "debug") Log.w(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (ENABLE_LOGS == "debug") Log.e(tag, message, throwable)
    }
}