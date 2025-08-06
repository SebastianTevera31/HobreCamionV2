package com.rfz.appflotal

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.rfz.appflotal.core.util.AppLocale
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class ProyectoFscApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLocale.loadSavedLocale(this)
    }
}