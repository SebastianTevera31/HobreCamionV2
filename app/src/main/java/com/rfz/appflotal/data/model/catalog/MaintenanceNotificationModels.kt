package com.rfz.appflotal.data.model.catalog

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/** Fila de `api/Catalog/Notifications`: recordatorio que se emite cada [daysConf] días. */
data class MaintenanceNotificationResponse(
    @SerializedName("id_notifications") val id: Int,
    @SerializedName("fld_notification") val message: String? = null,
    @SerializedName("fld_daysConf") val daysConf: Int = 0
)

/**
 * Recordatorio de mantenimiento del catálogo. El ciclo es fijo: vence [daysConf] días después
 * de la última emisión ([lastNotifiedAt]) o, si nunca se emitió, de [firstSeenAt].
 * [acknowledgedAt] ("Enterado") solo lo saca de la bandeja; no mueve el ciclo.
 */
@Entity(tableName = "maintenance_notification_table")
data class MaintenanceNotificationEntity(
    @PrimaryKey
    val id: Int,
    val message: String,
    val daysConf: Int,
    // Primera vez que la app recibió el recordatorio del endpoint.
    val firstSeenAt: Long,
    val lastNotifiedAt: Long? = null,
    val acknowledgedAt: Long? = null
)

/** Recordatorio pendiente en la bandeja de notificaciones. */
data class MaintenanceNotification(
    val id: Int,
    val message: String,
    val notifiedAt: Long
)
