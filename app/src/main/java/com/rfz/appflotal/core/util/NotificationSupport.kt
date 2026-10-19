package com.rfz.appflotal.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/** Permiso concedido y notificaciones de la app activadas. */
fun Context.canPostNotifications(): Boolean {
    val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    return hasPermission && NotificationManagerCompat.from(this).areNotificationsEnabled()
}

/** En segundo plano el contexto usa el idioma del sistema; este usa el de la app. */
fun Context.withAppLocale(): Context {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(AppLocale.currentLocale.value)
    return createConfigurationContext(configuration)
}
