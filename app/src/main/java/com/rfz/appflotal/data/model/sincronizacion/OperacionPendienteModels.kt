package com.rfz.appflotal.data.model.sincronizacion

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

/** id que devuelven CrudAssemblyTire, DisassemblyTire e InspectionTire en el cuerpo al aceptar. */
const val ID_OPERACION_ACEPTADA = 200

/** El payload se guarda tal como lo espera el servidor, para reenviarlo sin transformarlo. */
internal val gsonPayload = Gson()

enum class TipoOperacion { MONTAJE, DESMONTAJE, INSPECCION }

/** PENDIENTE con un CON_ERROR anterior en su posición se considera bloqueada (no se guarda aparte). */
enum class EstadoOperacion { PENDIENTE, ENVIANDO, CON_ERROR }

@Entity(tableName = "operacion_pendiente")
data class OperacionPendienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val idCliente: String,
    val tipo: TipoOperacion,
    val posicion: String,
    val idMonitor: Int,
    val idUsuario: Int,
    val idGrupo: String?,
    val payload: String,
    val estado: EstadoOperacion = EstadoOperacion.PENDIENTE,
    val intentos: Int = 0,
    val ultimoError: String? = null,
    val creadoEn: Long,
)

data class OperacionPendiente(
    val id: Long,
    val tipo: TipoOperacion,
    val posicion: String,
    val idGrupo: String?,
    val estado: EstadoOperacion,
    val intentos: Int,
    val ultimoError: String?,
    val creadoEn: Long,
)

fun OperacionPendienteEntity.toDomain() = OperacionPendiente(
    id = id,
    tipo = tipo,
    posicion = posicion,
    idGrupo = idGrupo,
    estado = estado,
    intentos = intentos,
    ultimoError = ultimoError,
    creadoEn = creadoEn
)

/** Respuesta de un envío al servidor. */
sealed interface ResultadoEnvio {
    data object Aceptada : ResultadoEnvio
    data class Rechazada(val mensaje: String?) : ResultadoEnvio
    data object ErrorConexion : ResultadoEnvio
    data object SesionVencida : ResultadoEnvio
}

/** Resultado de recorrer la cola una vez. */
enum class ResultadoProcesamiento { COMPLETA, REINTENTAR, SESION_VENCIDA, EN_CURSO }

/** Lo que recibe la pantalla al registrar una operación. */
sealed interface ResultadoOperacion {
    data object Enviada : ResultadoOperacion
    data object EnCola : ResultadoOperacion
    data class Rechazada(val mensaje: String?) : ResultadoOperacion
}

/** Enviada o en cola cuentan como registradas: la pantalla puede cerrarse. */
fun Result<ResultadoOperacion>.fueRegistrada(): Boolean =
    getOrNull().let { it != null && it !is ResultadoOperacion.Rechazada }

fun Result<ResultadoOperacion>.mensajeRechazo(): String? =
    (getOrNull() as? ResultadoOperacion.Rechazada)?.mensaje
