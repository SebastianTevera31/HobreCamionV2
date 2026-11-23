package com.rfz.appflotal.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Catálogos guardados en el teléfono para operar sin conexión. inspection_catalog pasa a
 * catalogo (tipo REPORTE_INSPECCION). El índice de dataframe evita que la consulta de la última
 * trama por sensor se vuelva más lenta conforme crece la tabla.
 */
val MIGRATION_35_36 = object : Migration(35, 36) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `catalogo` (" +
                "`tipo` TEXT NOT NULL, " +
                "`id` INTEGER NOT NULL, " +
                "`descripcion` TEXT NOT NULL, " +
                "`descripcionEn` TEXT, " +
                "`activo` INTEGER NOT NULL, " +
                "PRIMARY KEY(`tipo`, `id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `llanta` (" +
                "`idLlanta` INTEGER NOT NULL, " +
                "`proveedor` TEXT NOT NULL, " +
                "`medida` TEXT NOT NULL, " +
                "`marca` TEXT NOT NULL, " +
                "`modelo` TEXT NOT NULL, " +
                "`capacidadCarga` TEXT NOT NULL, " +
                "`destino` TEXT NOT NULL, " +
                "`tipoAdquisicion` TEXT NOT NULL, " +
                "`ultimaPosicionMontada` TEXT NOT NULL, " +
                "`ultimoDisenoRenovado` TEXT NOT NULL, " +
                "`fechaUltimaPosicionMontada` TEXT NOT NULL, " +
                "`numeroVehiculo` TEXT NOT NULL, " +
                "`fechaEventoMontaje` TEXT NOT NULL, " +
                "`fechaEventoA` TEXT NOT NULL, " +
                "`profundidadMontaje` REAL NOT NULL, " +
                "`odometroMontaje` INTEGER NOT NULL, " +
                "`tipoVehiculo` TEXT NOT NULL, " +
                "PRIMARY KEY(`idLlanta`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `descarga_catalogo` (" +
                "`tipo` TEXT NOT NULL, " +
                "`descargadoEn` INTEGER NOT NULL, " +
                "PRIMARY KEY(`tipo`))"
        )

        db.execSQL(
            "INSERT INTO `catalogo` (`tipo`, `id`, `descripcion`, `descripcionEn`, `activo`) " +
                "SELECT 'REPORTE_INSPECCION', `idTireInspectionReport`, `description`, NULL, `isActive` " +
                "FROM `inspection_catalog`"
        )
        db.execSQL("DROP TABLE IF EXISTS `inspection_catalog`")

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_dataframe_monitor_id_sensor_id_timestamp` " +
                "ON `dataframe` (`monitor_id`, `sensor_id`, `timestamp`)"
        )
    }
}
