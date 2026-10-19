package com.rfz.appflotal.data.repository.maintenance

import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationEntity
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class MaintenanceNotificationsLogicTest {

    private val day = TimeUnit.DAYS.toMillis(1)

    private fun response(id: Int, message: String? = "Mensaje $id", days: Int = 30) =
        MaintenanceNotificationResponse(id = id, message = message, daysConf = days)

    private fun entity(
        id: Int = 1,
        days: Int = 30,
        firstSeenAt: Long = 0,
        lastNotifiedAt: Long? = null,
        acknowledgedAt: Long? = null
    ) = MaintenanceNotificationEntity(
        id = id,
        message = "Mensaje $id",
        daysConf = days,
        firstSeenAt = firstSeenAt,
        lastNotifiedAt = lastNotifiedAt,
        acknowledgedAt = acknowledgedAt
    )

    // --- mergeMaintenanceCatalog ---

    @Test
    fun merge_nuevoRecordatorio_empiezaSuCicloAhora() {
        val merged = mergeMaintenanceCatalog(listOf(response(1)), emptyList(), now = 500)

        assertEquals(1, merged.size)
        assertEquals(500L, merged[0].firstSeenAt)
        assertNull(merged[0].lastNotifiedAt)
        assertNull(merged[0].acknowledgedAt)
    }

    @Test
    fun merge_recordatorioConocido_conservaFechasYActualizaTextoYDias() {
        val local = entity(id = 1, days = 30, firstSeenAt = 10, lastNotifiedAt = 20, acknowledgedAt = 30)

        val merged = mergeMaintenanceCatalog(
            listOf(response(1, message = "Texto nuevo", days = 45)),
            listOf(local),
            now = 999
        )

        val result = merged.single()
        assertEquals("Texto nuevo", result.message)
        assertEquals(45, result.daysConf)
        assertEquals(10L, result.firstSeenAt)
        assertEquals(20L, result.lastNotifiedAt)
        assertEquals(30L, result.acknowledgedAt)
    }

    @Test
    fun merge_idQueYaNoExpone_seElimina() {
        val merged = mergeMaintenanceCatalog(
            listOf(response(2)),
            listOf(entity(id = 1), entity(id = 2)),
            now = 0
        )

        assertEquals(listOf(2), merged.map { it.id })
    }

    @Test
    fun merge_listaVacia_eliminaTodo() {
        val merged = mergeMaintenanceCatalog(emptyList(), listOf(entity(id = 1)), now = 0)

        assertTrue(merged.isEmpty())
    }

    @Test
    fun merge_descartaSinMensajeODiasNoPositivos() {
        val merged = mergeMaintenanceCatalog(
            listOf(
                response(1, message = null),
                response(2, message = "   "),
                response(3, days = 0),
                response(4, days = -5),
                response(5)
            ),
            emptyList(),
            now = 0
        )

        assertEquals(listOf(5), merged.map { it.id })
    }

    @Test
    fun merge_idDuplicado_seQuedaConElPrimero() {
        val merged = mergeMaintenanceCatalog(
            listOf(response(1, message = "Primero"), response(1, message = "Segundo")),
            emptyList(),
            now = 0
        )

        assertEquals("Primero", merged.single().message)
    }

    @Test
    fun merge_recortaEspaciosDelMensaje() {
        val merged = mergeMaintenanceCatalog(listOf(response(1, message = "  Hola  ")), emptyList(), 0)

        assertEquals("Hola", merged.single().message)
    }

    // --- Vencimiento (ciclo fijo) ---

    @Test
    fun nuncaEmitido_venceAlCumplirDiasDesdeQueSeRecibio() {
        val notification = entity(days = 30, firstSeenAt = 1_000)

        assertFalse(notification.isDue(1_000 + 30 * day - 1))
        assertTrue(notification.isDue(1_000 + 30 * day))
    }

    @Test
    fun yaEmitido_venceAlCumplirDiasDesdeLaUltimaEmision() {
        val notification = entity(days = 30, firstSeenAt = 0, lastNotifiedAt = 40 * day)

        assertFalse(notification.isDue(69 * day))
        assertTrue(notification.isDue(70 * day))
    }

    @Test
    fun enterado_noMueveElCiclo() {
        // Emitido el día 30 y marcado "Enterado" el día 45: el siguiente sigue siendo el día 60.
        val notification = entity(days = 30, firstSeenAt = 0, lastNotifiedAt = 30 * day, acknowledgedAt = 45 * day)

        assertEquals(60 * day, notification.nextDueAt())
    }

    @Test
    fun cambioDeDias_recalculaElSiguienteVencimiento() {
        val before = entity(days = 30, firstSeenAt = 0, lastNotifiedAt = 10 * day)
        val after = before.copy(daysConf = 240)

        assertEquals(40 * day, before.nextDueAt())
        assertEquals(250 * day, after.nextDueAt())
    }

    @Test
    fun diasGrandes_noDesbordan() {
        val notification = entity(days = Int.MAX_VALUE, firstSeenAt = 0)

        assertTrue(notification.nextDueAt() > 0)
    }

    @Test
    fun dueMaintenanceNotifications_soloDevuelveLosVencidos() {
        val due = dueMaintenanceNotifications(
            listOf(
                entity(id = 1, days = 30, firstSeenAt = 0),
                entity(id = 2, days = 240, firstSeenAt = 0)
            ),
            now = 31 * day
        )

        assertEquals(listOf(1), due.map { it.id })
    }

    // --- Pendientes de la bandeja ---

    @Test
    fun pendiente_soloSiSeEmitioYNoTieneEnteradoPosterior() {
        assertFalse(entity(lastNotifiedAt = null).isPending())
        assertTrue(entity(lastNotifiedAt = 100).isPending())
        assertFalse(entity(lastNotifiedAt = 100, acknowledgedAt = 150).isPending())
        assertFalse(entity(lastNotifiedAt = 100, acknowledgedAt = 100).isPending())
        // Volvió a vencer después del "Enterado": reaparece.
        assertTrue(entity(lastNotifiedAt = 200, acknowledgedAt = 150).isPending())
    }

    @Test
    fun pendingMaintenanceNotifications_ordenaLaMasRecientePrimero() {
        val pending = pendingMaintenanceNotifications(
            listOf(
                entity(id = 1, lastNotifiedAt = 100),
                entity(id = 2, lastNotifiedAt = 300),
                entity(id = 3, lastNotifiedAt = 200, acknowledgedAt = 250),
                entity(id = 4)
            )
        )

        assertEquals(listOf(2, 1), pending.map { it.id })
        assertEquals(300L, pending.first().notifiedAt)
    }
}
