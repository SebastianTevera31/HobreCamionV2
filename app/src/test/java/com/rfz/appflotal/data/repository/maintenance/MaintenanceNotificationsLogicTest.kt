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
        acknowledgedAt: Long? = null,
        baselineOdometerKm: Int? = null
    ) = MaintenanceNotificationEntity(
        id = id,
        message = "Mensaje $id",
        daysConf = days,
        firstSeenAt = firstSeenAt,
        lastNotifiedAt = lastNotifiedAt,
        acknowledgedAt = acknowledgedAt,
        baselineOdometerKm = baselineOdometerKm
    )

    // Alineación: 240 días o 120,000 km.
    private fun alignment(
        firstSeenAt: Long = 0,
        lastNotifiedAt: Long? = null,
        acknowledgedAt: Long? = null,
        baselineOdometerKm: Int? = null
    ) = entity(
        id = WHEEL_ALIGNMENT_NOTIFICATION_ID,
        days = 240,
        firstSeenAt = firstSeenAt,
        lastNotifiedAt = lastNotifiedAt,
        acknowledgedAt = acknowledgedAt,
        baselineOdometerKm = baselineOdometerKm
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
        val local = entity(
            id = 1, days = 30, firstSeenAt = 10, lastNotifiedAt = 20, acknowledgedAt = 30,
            baselineOdometerKm = 50_000
        )

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
        assertEquals(50_000, result.baselineOdometerKm)
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

        assertFalse(notification.isDueByDays(1_000 + 30 * day - 1))
        assertTrue(notification.isDueByDays(1_000 + 30 * day))
    }

    @Test
    fun yaEmitido_venceAlCumplirDiasDesdeLaUltimaEmision() {
        val notification = entity(days = 30, firstSeenAt = 0, lastNotifiedAt = 40 * day)

        assertFalse(notification.isDueByDays(69 * day))
        assertTrue(notification.isDueByDays(70 * day))
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
    fun evaluate_soloEmiteLosVencidosYRegistraLaEmision() {
        val evaluation = evaluateMaintenanceNotifications(
            listOf(
                entity(id = 1, days = 30, firstSeenAt = 0),
                entity(id = 3, days = 240, firstSeenAt = 0)
            ),
            now = 31 * day,
            odometerKm = null
        )

        assertEquals(listOf(1), evaluation.emitted.map { it.id })
        assertEquals(31 * day, evaluation.emitted.single().lastNotifiedAt)
        assertEquals(listOf(1), evaluation.updated.map { it.id })
    }

    @Test
    fun evaluate_sinCambios_noDevuelveFilasParaGuardar() {
        val evaluation = evaluateMaintenanceNotifications(
            listOf(entity(id = 1, days = 30, firstSeenAt = 0)),
            now = day,
            odometerKm = 10_000
        )

        assertTrue(evaluation.emitted.isEmpty())
        assertTrue(evaluation.updated.isEmpty())
    }

    // --- Regla de kilometraje (alineación) ---

    @Test
    fun kmConf_soloAplicaALaAlineacion() {
        assertEquals(120_000, kmConfFor(WHEEL_ALIGNMENT_NOTIFICATION_ID))
        assertNull(kmConfFor(1))
    }

    @Test
    fun alineacion_vencePorKmAntesQuePorDias() {
        val notification = alignment(firstSeenAt = 0, baselineOdometerKm = 100_000)

        assertFalse(notification.isDue(now = 10 * day, odometerKm = 219_999))
        assertTrue(notification.isDue(now = 10 * day, odometerKm = 220_000))
    }

    @Test
    fun alineacion_vencePorDiasAunqueNoLlegueAlKm() {
        val notification = alignment(firstSeenAt = 0, baselineOdometerKm = 100_000)

        assertTrue(notification.isDue(now = 240 * day, odometerKm = 100_500))
    }

    @Test
    fun alineacion_sinReferenciaOSinOdometro_soloAplicanLosDias() {
        assertFalse(alignment(baselineOdometerKm = null).isDueByKm(500_000))
        assertFalse(alignment(baselineOdometerKm = 100_000).isDueByKm(null))
        assertFalse(alignment(baselineOdometerKm = 100_000).isDueByKm(0))
    }

    @Test
    fun recordatorioSinReglaDeKm_ignoraElOdometro() {
        val notification = entity(id = 1, days = 30, baselineOdometerKm = 0)

        assertFalse(notification.isDueByKm(1_000_000))
    }

    @Test
    fun referencia_seIniciaConElPrimerOdometroValido() {
        assertNull(alignment().withOdometerBaseline(0).baselineOdometerKm)
        assertNull(alignment().withOdometerBaseline(null).baselineOdometerKm)
        assertEquals(80_000, alignment().withOdometerBaseline(80_000).baselineOdometerKm)
    }

    @Test
    fun referencia_siElOdometroBaja_seReiniciaAlActual() {
        val notification = alignment(baselineOdometerKm = 300_000)

        assertEquals(15_000, notification.withOdometerBaseline(15_000).baselineOdometerKm)
        assertEquals(300_000, notification.withOdometerBaseline(350_000).baselineOdometerKm)
    }

    @Test
    fun referencia_noSeAsignaARecordatoriosSinReglaDeKm() {
        assertNull(entity(id = 1).withOdometerBaseline(80_000).baselineOdometerKm)
    }

    @Test
    fun evaluate_primerOdometro_soloGuardaLaReferenciaSinEmitir() {
        val evaluation = evaluateMaintenanceNotifications(
            listOf(alignment(firstSeenAt = 0)),
            now = day,
            odometerKm = 80_000
        )

        assertTrue(evaluation.emitted.isEmpty())
        assertEquals(80_000, evaluation.updated.single().baselineOdometerKm)
    }

    @Test
    fun evaluate_emisionPorKm_reiniciaAmbosCiclos() {
        val evaluation = evaluateMaintenanceNotifications(
            listOf(alignment(firstSeenAt = 0, baselineOdometerKm = 100_000)),
            now = 50 * day,
            odometerKm = 225_000
        )

        val emitted = evaluation.emitted.single()
        assertEquals(50 * day, emitted.lastNotifiedAt)
        assertEquals(225_000, emitted.baselineOdometerKm)
        // Siguiente aviso: 240 días después de esta emisión o 120,000 km después de 225,000.
        assertEquals(290 * day, emitted.nextDueAt())
        assertFalse(emitted.isDueByKm(344_999))
        assertTrue(emitted.isDueByKm(345_000))
    }

    @Test
    fun evaluate_emisionPorDias_tambienReiniciaLaReferenciaDeKm() {
        val evaluation = evaluateMaintenanceNotifications(
            listOf(alignment(firstSeenAt = 0, baselineOdometerKm = 100_000)),
            now = 240 * day,
            odometerKm = 150_000
        )

        assertEquals(150_000, evaluation.emitted.single().baselineOdometerKm)
    }

    @Test
    fun evaluate_emisionSinOdometro_conservaLaReferencia() {
        val evaluation = evaluateMaintenanceNotifications(
            listOf(alignment(firstSeenAt = 0, baselineOdometerKm = 100_000)),
            now = 240 * day,
            odometerKm = 0
        )

        assertEquals(100_000, evaluation.emitted.single().baselineOdometerKm)
    }

    @Test
    fun alineacion_enterado_noMueveNingunCiclo() {
        val emitted = alignment(firstSeenAt = 0, lastNotifiedAt = 10 * day, baselineOdometerKm = 200_000)
            .copy(acknowledgedAt = 20 * day)

        assertEquals(250 * day, emitted.nextDueAt())
        assertTrue(emitted.isDueByKm(320_000))
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
