package com.rfz.appflotal.data.model.catalogo

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Datos que se guardan en el teléfono para montar, desmontar e inspeccionar sin conexión. */
enum class TipoDatoOffline { LLANTA, DESTINO, CAUSA_DESMONTAJE, REPORTE_INSPECCION }

/** Listas de id y descripción: destinos, causas de desmontaje y reportes de inspección. */
@Entity(tableName = "catalogo", primaryKeys = ["tipo", "id"])
data class CatalogoEntity(
    val tipo: TipoDatoOffline,
    val id: Int,
    /** Tal como la envía el servidor (en el idioma del token). */
    val descripcion: String,
    /** Solo los destinos traen la descripción en inglés aparte. */
    val descripcionEn: String? = null,
    val activo: Boolean = true,
)

/** Inventario de llantas de la flota, con los campos de api/Tire/TireList. */
@Entity(tableName = "llanta")
data class LlantaEntity(
    @PrimaryKey val idLlanta: Int,
    val proveedor: String,
    val medida: String,
    val marca: String,
    val modelo: String,
    val capacidadCarga: String,
    val destino: String,
    val tipoAdquisicion: String,
    val ultimaPosicionMontada: String,
    val ultimoDisenoRenovado: String,
    val fechaUltimaPosicionMontada: String,
    val numeroVehiculo: String,
    val fechaEventoMontaje: String,
    val fechaEventoA: String,
    val profundidadMontaje: Double,
    val odometroMontaje: Int,
    val tipoVehiculo: String,
)

/** Cuándo se descargó por última vez cada tipo de dato. */
@Entity(tableName = "descarga_catalogo")
data class DescargaCatalogoEntity(
    @PrimaryKey val tipo: TipoDatoOffline,
    val descargadoEn: Long,
)
