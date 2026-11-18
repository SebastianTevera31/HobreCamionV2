package com.rfz.appflotal.data.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.JsonObject
import java.util.UUID

/**
 * Cola única de operaciones pendientes (montaje, desmontaje, inspección). Los pendientes de
 * disassembly_tire_table e inspection_tire_table se pasan a la cola antes de borrar esas tablas.
 */
val MIGRATION_34_35 = object : Migration(34, 35) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `operacion_pendiente` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`idCliente` TEXT NOT NULL, " +
                "`tipo` TEXT NOT NULL, " +
                "`posicion` TEXT NOT NULL, " +
                "`idMonitor` INTEGER NOT NULL, " +
                "`idUsuario` INTEGER NOT NULL, " +
                "`idGrupo` TEXT, " +
                "`payload` TEXT NOT NULL, " +
                "`estado` TEXT NOT NULL, " +
                "`intentos` INTEGER NOT NULL, " +
                "`ultimoError` TEXT, " +
                "`creadoEn` INTEGER NOT NULL)"
        )

        val usuario = db.query("SELECT idUser, id_monitor FROM user LIMIT 1").use {
            if (it.moveToFirst()) it.getInt(0) to it.getInt(1) else null
        }
        // Sin sesión no hay a nombre de quién enviarlos; la app ya los habría descartado al salir.
        if (usuario != null) {
            val pendientes = inspeccionesHeredadas(db) + desmontajesHeredados(db)
            // La inspección del desmontaje se guardaba primero; a igual hora va antes.
            pendientes.sortedWith(compareBy({ it.actualizadoEn }, { it.tipo != "INSPECCION" }))
                .forEach { db.insertarPendiente(it, idUsuario = usuario.first, idMonitor = usuario.second) }
        }

        db.execSQL("DROP TABLE IF EXISTS `disassembly_tire_table`")
        db.execSQL("DROP TABLE IF EXISTS `inspection_tire_table`")
    }
}

private class PendienteHeredado(
    val tipo: String,
    val posicion: String,
    val payload: String,
    val actualizadoEn: Long,
)

/** Las claves del JSON son las del cuerpo que espera api/InspectionTire. */
private fun inspeccionesHeredadas(db: SupportSQLiteDatabase): List<PendienteHeredado> =
    db.query("SELECT * FROM inspection_tire_table").use { c ->
        buildList {
            while (c.moveToNext()) {
                val payload = JsonObject().apply {
                    addProperty("positionTire", c.texto("positionTire"))
                    addProperty("fld_treadDepth", c.decimal("treadDepth"))
                    addProperty("fld_treadDepth2", c.decimal("treadDepth2"))
                    addProperty("fld_treadDepth3", c.decimal("treadDepth3"))
                    addProperty("fld_treadDepth4", c.decimal("treadDepth4"))
                    addProperty("c_tireInspectionReport_fk_4", c.entero("tireInspectionReportId"))
                    addProperty("fld_pressureInspected", c.entero("pressureInspected"))
                    addProperty("fld_dateInspection", c.texto("dateInspection"))
                    addProperty("fld_odometer", c.entero("odometer"))
                    addProperty("fld_temperatureInspected", c.entero("temperatureInspected"))
                    addProperty("fld_pressureAdjusted", c.entero("pressureAdjusted"))
                }
                add(PendienteHeredado("INSPECCION", c.texto("positionTire"), payload.toString(), c.largo("updatedAt")))
            }
        }
    }

/** Las claves del JSON son las del cuerpo que espera api/Disassembly/DisassemblyTire. */
private fun desmontajesHeredados(db: SupportSQLiteDatabase): List<PendienteHeredado> =
    db.query("SELECT * FROM disassembly_tire_table").use { c ->
        buildList {
            while (c.moveToNext()) {
                val payload = JsonObject().apply {
                    addProperty("c_disassemblyCauses_fk_1", c.entero("disassemblyCause"))
                    addProperty("c_destination_fk_3", c.entero("destination"))
                    addProperty("fld_dateOperation", c.texto("dateOperation"))
                    addProperty("positionTire", c.texto("positionTire"))
                    addProperty("fld_odometer", c.entero("odometer"))
                }
                add(PendienteHeredado("DESMONTAJE", c.texto("positionTire"), payload.toString(), c.largo("updatedAt")))
            }
        }
    }

private fun SupportSQLiteDatabase.insertarPendiente(p: PendienteHeredado, idUsuario: Int, idMonitor: Int) {
    val valores = ContentValues().apply {
        put("idCliente", UUID.randomUUID().toString())
        put("tipo", p.tipo)
        put("posicion", p.posicion)
        put("idMonitor", idMonitor)
        put("idUsuario", idUsuario)
        putNull("idGrupo")
        put("payload", p.payload)
        put("estado", "PENDIENTE")
        put("intentos", 0)
        putNull("ultimoError")
        put("creadoEn", p.actualizadoEn)
    }
    insert("operacion_pendiente", SQLiteDatabase.CONFLICT_ABORT, valores)
}

private fun Cursor.texto(columna: String): String = getString(getColumnIndexOrThrow(columna))
private fun Cursor.entero(columna: String): Int = getInt(getColumnIndexOrThrow(columna))
private fun Cursor.largo(columna: String): Long = getLong(getColumnIndexOrThrow(columna))
private fun Cursor.decimal(columna: String): Float = getFloat(getColumnIndexOrThrow(columna))
