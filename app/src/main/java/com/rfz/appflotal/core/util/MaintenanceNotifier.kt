package com.rfz.appflotal.core.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.catalog.MaintenanceNotification
import com.rfz.appflotal.presentation.ui.inicio.ui.InicioActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAINTENANCE_CHANNEL_ID = "maintenance_reminders_channel"

// Las notificaciones se publican con este tag y el id del recordatorio, para no chocar con
// los ids de otras notificaciones de la app (p. ej. las de accesorios seguidos).
private const val MAINTENANCE_NOTIFICATION_TAG = "maintenance_reminder"
private const val MAINTENANCE_REMINDER_ACTION = "com.rfz.appflotal.MAINTENANCE_REMINDER"

@Singleton
class MaintenanceNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Publica una notificación por recordatorio; cada una reemplaza la anterior del mismo id.
     * Devuelve false si no hay permiso o están desactivadas.
     */
    @SuppressLint("MissingPermission") // El permiso se verifica en canPostNotifications().
    fun notify(notifications: List<MaintenanceNotification>): Boolean {
        if (notifications.isEmpty() || !context.canPostNotifications()) return false

        val localized = context.withAppLocale()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(
            NotificationChannel(
                MAINTENANCE_CHANNEL_ID,
                localized.getString(R.string.canal_recordatorios_mantenimiento),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        return try {
            notifications.forEach { notification ->
                notificationManager.notify(
                    MAINTENANCE_NOTIFICATION_TAG,
                    notification.id,
                    buildNotification(localized, notification)
                )
            }
            true
        } catch (_: SecurityException) {
            // El permiso se revocó entre la verificación y la publicación.
            false
        }
    }

    fun cancel(ids: Collection<Int>) {
        val notificationManager = NotificationManagerCompat.from(context)
        ids.forEach { id -> notificationManager.cancel(MAINTENANCE_NOTIFICATION_TAG, id) }
    }

    /** Al cerrar sesión: retira los recordatorios que sigan en la barra de notificaciones. */
    fun cancelAll() {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
            ?: return
        notificationManager.activeNotifications
            .filter { it.tag == MAINTENANCE_NOTIFICATION_TAG }
            .forEach { notificationManager.cancel(it.tag, it.id) }
    }

    private fun buildNotification(
        localized: Context,
        notification: MaintenanceNotification
    ): Notification = NotificationCompat.Builder(context, MAINTENANCE_CHANNEL_ID)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(localized.getString(R.string.notificacion_mantenimiento_titulo))
        .setContentText(notification.message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(openAppIntent(notification.id))
        .build()

    /** Abre la app en el inicio; si ya está abierta, reutiliza la activity. */
    private fun openAppIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, InicioActivity::class.java)
            // Acción propia: sin ella, un requestCode igual al de una notificación de promociones
            // reemplazaría su PendingIntent (los extras no cuentan para distinguirlos).
            .setAction(MAINTENANCE_REMINDER_ACTION)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
