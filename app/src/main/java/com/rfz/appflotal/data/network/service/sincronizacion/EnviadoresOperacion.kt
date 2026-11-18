package com.rfz.appflotal.data.network.service.sincronizacion

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTireRequestDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.sincronizacion.ID_OPERACION_ACEPTADA
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.data.model.sincronizacion.gsonPayload
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.network.service.DataError
import com.rfz.appflotal.data.network.service.assembly.RemoteAssemblyDataSource
import com.rfz.appflotal.data.network.service.disassembly.RemoteDisassemblyTireDataSource
import javax.inject.Inject

/** Envía al servidor un tipo de operación guardada en la cola. */
interface EnviadorOperacion {
    suspend fun enviar(token: String, payload: String): ResultadoEnvio

    /** Si el servidor ya tiene aplicada la operación (p. ej. se perdió la respuesta); null si no se sabe. */
    suspend fun yaAplicada(token: String, idMonitor: Int, payload: String): Boolean?
}

/**
 * El servidor responde HTTP 200 también al rechazar; el resultado real es el id del cuerpo.
 * Un 4xx tampoco se arregla reintentando, salvo el 401, que pide volver a iniciar sesión.
 */
fun Result<List<GeneralResponse>>.aResultadoEnvio(): ResultadoEnvio = fold(
    onSuccess = { respuesta ->
        val primera = respuesta.firstOrNull()
        if (primera?.id == ID_OPERACION_ACEPTADA) ResultadoEnvio.Aceptada
        else ResultadoEnvio.Rechazada(primera?.message)
    },
    onFailure = { error ->
        when {
            error is DataError.Http && error.code == 401 -> ResultadoEnvio.SesionVencida
            error is DataError.Http && error.code in 400..499 -> ResultadoEnvio.Rechazada(error.body)
            else -> ResultadoEnvio.ErrorConexion
        }
    }
)

class EnviadorMontaje @Inject constructor(
    private val remoto: RemoteAssemblyDataSource,
) : EnviadorOperacion {
    override suspend fun enviar(token: String, payload: String) =
        remoto.pushAssemblyTire(token, payload.comoMontaje()).aResultadoEnvio()

    override suspend fun yaAplicada(token: String, idMonitor: Int, payload: String): Boolean? {
        val montaje = payload.comoMontaje()
        return remoto.fetchMountedTire(token, idMonitor).getOrNull()
            ?.any { it.positionTire == montaje.positionTire && it.tireId == montaje.idTire }
    }

    private fun String.comoMontaje() = gsonPayload.fromJson(this, AssemblyTireDto::class.java)
}

class EnviadorDesmontaje @Inject constructor(
    private val remoto: RemoteDisassemblyTireDataSource,
    private val remotoMontajes: RemoteAssemblyDataSource,
) : EnviadorOperacion {
    override suspend fun enviar(token: String, payload: String) =
        remoto.createDisassemblyTire(token, payload.comoDesmontaje()).aResultadoEnvio()

    override suspend fun yaAplicada(token: String, idMonitor: Int, payload: String): Boolean? {
        val posicion = payload.comoDesmontaje().positionTire
        return remotoMontajes.fetchMountedTire(token, idMonitor).getOrNull()
            ?.none { it.positionTire == posicion }
    }

    private fun String.comoDesmontaje() =
        gsonPayload.fromJson(this, DisassemblyTireRequestDto::class.java)
}

class EnviadorInspeccion @Inject constructor(
    private val remoto: RemoteInspeccionDataSource,
) : EnviadorOperacion {
    override suspend fun enviar(token: String, payload: String) =
        remoto.enviarInspeccion(token, gsonPayload.fromJson(payload, InspectionTireDto::class.java))
            .aResultadoEnvio()

    // El servidor no expone las inspecciones registradas; no hay forma de comprobarlo.
    override suspend fun yaAplicada(token: String, idMonitor: Int, payload: String): Boolean? = null
}

class EnviadoresOperacion @Inject constructor(
    private val montaje: EnviadorMontaje,
    private val desmontaje: EnviadorDesmontaje,
    private val inspeccion: EnviadorInspeccion,
) {
    fun para(tipo: TipoOperacion): EnviadorOperacion = when (tipo) {
        TipoOperacion.MONTAJE -> montaje
        TipoOperacion.DESMONTAJE -> desmontaje
        TipoOperacion.INSPECCION -> inspeccion
    }
}
