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
 * de la última emisión ([lastNotifiedAt]) o, si nunca se emitió, de [firstSeenAt]. Los que
 * además tienen regla de kilometraje (alineación) vencen antes si la unidad recorre su límite
 * desde [baselineOdometerKm]. [acknowledgedAt] ("Enterado") solo lo saca de la bandeja; no
 * mueve el ciclo.
 */
@Entity(tableName = "maintenance_notification_table")
data class MaintenanceNotificationEntity(
    @PrimaryKey
    val id: Int,
    val message: String,
    val daysConf: Int,
    val firstSeenAt: Long,
    val lastNotifiedAt: Long? = null,
    val acknowledgedAt: Long? = null,
    val baselineOdometerKm: Int? = null
)

/** Recordatorio pendiente en la bandeja de notificaciones. */
data class MaintenanceNotification(
    val id: Int,
    val message: String,
    val notifiedAt: Long
)
