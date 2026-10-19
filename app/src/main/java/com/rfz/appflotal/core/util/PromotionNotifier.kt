package com.rfz.appflotal.core.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.promotions.NewFollowedPromotion
import com.rfz.appflotal.data.repository.promotions.AccessoryNotification
import com.rfz.appflotal.data.repository.promotions.MAX_NOTIFIED_ACCESSORIES
import com.rfz.appflotal.data.repository.promotions.buildAccessoryNotifications
import com.rfz.appflotal.data.repository.promotions.safeProductUrl
import com.rfz.appflotal.presentation.ui.couponbook.screen.promotions.formatPromotionPrice
import com.rfz.appflotal.presentation.ui.inicio.ui.InicioActivity
import com.rfz.appflotal.presentation.ui.inicio.ui.followedAccessoryDestination
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

private const val FOLLOWED_PROMOTIONS_CHANNEL_ID = "followed_promotions_channel"
private const val FOLLOWED_PROMOTIONS_GROUP = "com.rfz.appflotal.FOLLOWED_PROMOTIONS"

// Id fijo del resumen; cada accesorio usa su propio id para reemplazar su notificación anterior.
private const val SUMMARY_NOTIFICATION_ID = 2001
private const val ACCESSORY_NOTIFICATION_BASE_ID = 3000

private const val IMAGE_TIMEOUT_MILLIS = 5_000L
private const val IMAGE_SIZE_PX = 512

@Singleton
class PromotionNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Publica una notificación por accesorio (máximo [MAX_NOTIFIED_ACCESSORIES]) con su mejor
     * oferta. Con más de un accesorio se agrupan bajo un resumen. Devuelve false si no hay
     * permiso o están desactivadas.
     */
    @SuppressLint("MissingPermission") // El permiso se verifica en canPostNotifications().
    suspend fun notifyNewPromotions(promotions: List<NewFollowedPromotion>): Boolean {
        if (promotions.isEmpty() || !context.canPostNotifications()) return false

        val localized = context.withAppLocale()
        val notificationManager = NotificationManagerCompat.from(context)
        createChannel(localized, notificationManager)

        val entries = buildAccessoryNotifications(promotions)
        val shown = entries.take(MAX_NOTIFIED_ACCESSORIES)
        val grouped = entries.size > 1

        // Se descargan antes de publicar para no dejar el grupo a medias si tarda una imagen.
        val notifications = shown.map { entry ->
            accessoryNotificationId(entry.accessoryId) to buildAccessoryNotification(
                localized = localized,
                entry = entry,
                image = loadImage(entry.best.imageUrl),
                grouped = grouped
            )
        }

        return try {
            notifications.forEach { (id, notification) ->
                notificationManager.notify(
                    id,
                    notification
                )
            }
            if (grouped) {
                notificationManager.notify(
                    SUMMARY_NOTIFICATION_ID,
                    buildSummaryNotification(localized, entries)
                )
            }
            true
        } catch (_: SecurityException) {
            // El permiso se revocó entre la verificación y la publicación.
            false
        }
    }

    private fun buildAccessoryNotification(
        localized: Context,
        entry: AccessoryNotification,
        image: Bitmap?,
        grouped: Boolean
    ): Notification {
        val best = entry.best
        val title = localized.getString(
            R.string.notificacion_accesorio_titulo,
            entry.accessoryName,
            best.percentOff.toInt()
        )
        val text = localized.getString(
            R.string.notificacion_accesorio_texto,
            best.title,
            best.store,
            formatPromotionPrice(best.finalPrice, best.currency)
        )
        val openAccessory = deepLinkIntent(
            requestCode = accessoryNotificationId(entry.accessoryId),
            destination = followedAccessoryDestination(entry.accessoryId)
        )

        val builder = NotificationCompat.Builder(context, FOLLOWED_PROMOTIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(text)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAccessory)

        if (entry.newCount > 1) {
            builder.setSubText(
                localized.resources.getQuantityString(
                    R.plurals.notificacion_promociones_extra,
                    entry.newCount - 1,
                    entry.newCount - 1
                )
            )
        }

        if (image != null) {
            // Cerrada: miniatura a la derecha. Abierta: imagen grande sin repetir la miniatura.
            builder.setLargeIcon(image)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null as Bitmap?)
                        .setSummaryText(text)
                )
        } else {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
        }

        safeProductUrl(best.productUrl)?.let { url ->
            builder.addAction(
                0,
                localized.getString(R.string.accion_ver_oferta),
                viewUrlIntent(entry, url)
            )
        }
        builder.addAction(0, localized.getString(R.string.accion_ver_todas), openAccessory)

        if (grouped) {
            // Suena una sola vez (el resumen), no una por accesorio.
            builder.setGroup(FOLLOWED_PROMOTIONS_GROUP)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
        }
        return builder.build()
    }

    private fun buildSummaryNotification(
        localized: Context,
        entries: List<AccessoryNotification>
    ): Notification {
        val summary = localized.resources.getQuantityString(
            R.plurals.notificacion_resumen_accesorios,
            entries.size,
            entries.size
        )
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(localized.getString(R.string.notificacion_promociones_titulo))
        entries.take(MAX_NOTIFIED_ACCESSORIES).forEach { entry ->
            inboxStyle.addLine("${entry.accessoryName} · -${entry.best.percentOff.toInt()}%")
        }
        val hidden = entries.size - MAX_NOTIFIED_ACCESSORIES
        if (hidden > 0) {
            inboxStyle.setSummaryText(
                localized.resources.getQuantityString(
                    R.plurals.notificacion_accesorios_extra,
                    hidden,
                    hidden
                )
            )
        }

        return NotificationCompat.Builder(context, FOLLOWED_PROMOTIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(localized.getString(R.string.notificacion_promociones_titulo))
            .setContentText(summary)
            .setStyle(inboxStyle)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setGroup(FOLLOWED_PROMOTIONS_GROUP)
            .setGroupSummary(true)
            .setContentIntent(
                deepLinkIntent(
                    requestCode = SUMMARY_NOTIFICATION_ID,
                    destination = InicioActivity.DESTINO_FOLLOW_PROMOTIONS
                )
            )
            .build()
    }

    /** Imagen del producto o null si no es http(s), falla o tarda más de [IMAGE_TIMEOUT_MILLIS]. */
    private suspend fun loadImage(url: String?): Bitmap? {
        val safeUrl = safeProductUrl(url) ?: return null
        return withTimeoutOrNull(IMAGE_TIMEOUT_MILLIS.milliseconds) {
            val request = ImageRequest.Builder(context)
                .data(safeUrl)
                .size(IMAGE_SIZE_PX)
                // Las notificaciones no aceptan bitmaps de hardware.
                .allowHardware(false)
                .build()
            (context.imageLoader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
        }
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

    private fun accessoryNotificationId(accessoryId: Long): Int =
        ACCESSORY_NOTIFICATION_BASE_ID + (accessoryId % 100_000).toInt()

    /** Abre la app en [destination]; si ya está abierta, reutiliza la activity (onNewIntent). */
    private fun deepLinkIntent(requestCode: Int, destination: String): PendingIntent {
        val intent = Intent(context, InicioActivity::class.java)
            .putExtra(InicioActivity.EXTRA_DESTINO, destination)
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

    /** "Ver oferta": abre la página del producto en el navegador. */
    private fun viewUrlIntent(entry: AccessoryNotification, url: String): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context,
            // Distinto del de "Ver todas" para que un PendingIntent no reemplace al otro.
            -accessoryNotificationId(entry.accessoryId),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
