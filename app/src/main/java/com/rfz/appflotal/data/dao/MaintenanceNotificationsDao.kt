package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationEntity
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import com.rfz.appflotal.data.repository.maintenance.evaluateMaintenanceNotifications
import com.rfz.appflotal.data.repository.maintenance.mergeMaintenanceCatalog
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceNotificationsDao {

    @Query("SELECT * FROM maintenance_notification_table")
    fun observeAll(): Flow<List<MaintenanceNotificationEntity>>

    @Query("SELECT * FROM maintenance_notification_table")
    suspend fun getAll(): List<MaintenanceNotificationEntity>

    @Upsert
    suspend fun upsert(notifications: List<MaintenanceNotificationEntity>)

    @Query("DELETE FROM maintenance_notification_table WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("UPDATE maintenance_notification_table SET acknowledgedAt = :acknowledgedAt WHERE id = :id")
    suspend fun acknowledge(id: Int, acknowledgedAt: Long)

    @Query("DELETE FROM maintenance_notification_table")
    suspend fun clearAll()

    /**
     * Reemplaza el catálogo con la respuesta del endpoint y devuelve los ids eliminados.
     * En una transacción para no pisar una emisión que se registre mientras tanto.
     */
    @Transaction
    suspend fun applyCatalog(remote: List<MaintenanceNotificationResponse>, now: Long): List<Int> {
        val local = getAll()
        val merged = mergeMaintenanceCatalog(remote, local, now)
        val keptIds = merged.map { it.id }.toSet()
        val removedIds = local.map { it.id }.filterNot { it in keptIds }
        if (removedIds.isNotEmpty()) deleteByIds(removedIds)
        if (merged.isNotEmpty()) upsert(merged)
        return removedIds
    }

    /**
     * Registra como emitidos los recordatorios vencidos (por días o por kilometraje con
     * [odometerKm]) y los devuelve. En una transacción para que dos ejecuciones simultáneas no
     * emitan el mismo recordatorio dos veces.
     */
    @Transaction
    suspend fun takeDue(now: Long, odometerKm: Int?): List<MaintenanceNotificationEntity> {
        val evaluation = evaluateMaintenanceNotifications(getAll(), now, odometerKm)
        if (evaluation.updated.isNotEmpty()) upsert(evaluation.updated)
        return evaluation.emitted
    }
}
