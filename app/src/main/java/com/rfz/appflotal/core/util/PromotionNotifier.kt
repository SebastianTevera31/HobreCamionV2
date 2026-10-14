package com.rfz.appflotal.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.promotions.NewFollowedPromotion
import com.rfz.appflotal.presentation.ui.inicio.ui.InicioActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val FOLLOWED_PROMOTIONS_CHANNEL_ID = "followed_promotions_channel"

// Id fijo: una notificación nueva reemplaza a la anterior en lugar de acumularse.
private const val FOLLOWED_PROMOTIONS_NOTIFICATION_ID = 2001
private const val MAX_INBOX_LINES = 5

@Singleton
class PromotionNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /** Publica una sola notificación agrupada. Devuelve false si no hay permiso o están desactivadas. */
    @SuppressLint("MissingPermission") // El permiso se verifica en canNotify().
    fun notifyNewPromotions(promotions: List<NewFollowedPromotion>): Boolean {
        if (promotions.isEmpty() || !canNotify()) return false

        // En segundo plano el contexto usa el idioma del sistema; se fuerza el de la app.
        val localized = localizedContext()
        val notificationManager = NotificationManagerCompat.from(context)
        createChannel(localized, notificationManager)

        val accessoryNames = promotions.map { it.accessoryName }.distinct()
        val summary = localized.resources.getQuantityString(
            R.plurals.notificacion_promociones_nuevas,
            promotions.size,
            promotions.size,
            accessoryNames.joinToString(", ")
        )

        val inboxStyle = NotificationCompat.InboxStyle().setSummaryText(summary)
        promotions.take(MAX_INBOX_LINES).forEach { promotion ->
            inboxStyle.addLine(
                "${promotion.accessoryName}: ${promotion.discount.title} " +
                    "(-${promotion.discount.percentOff.toInt()}%)"
            )
        }

        val notification = NotificationCompat.Builder(context, FOLLOWED_PROMOTIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(localized.getString(R.string.notificacion_promociones_titulo))
            .setContentText(summary)
            .setStyle(inboxStyle)
            .setNumber(promotions.size)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent())
            .build()

        return try {
            notificationManager.notify(FOLLOWED_PROMOTIONS_NOTIFICATION_ID, notification)
            true
        } catch (_: SecurityException) {
            // El permiso se revocó entre la verificación y la publicación.
            false
        }
    }

    private fun canNotify(): Boolean {
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        return hasPermission && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun createChannel(localized: Context, notificationManager: NotificationManagerCompat) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                FOLLOWED_PROMOTIONS_CHANNEL_ID,
                localized.getString(R.string.canal_promociones_seguidas),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    private fun localizedContext(): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(AppLocale.currentLocale.value)
        return context.createConfigurationContext(configuration)
    }

    /** Abre la app como el ícono del launcher: si ya está abierta, la trae al frente sin reiniciarla. */
    private fun openAppIntent(): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: Intent(context, InicioActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context,
            FOLLOWED_PROMOTIONS_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
