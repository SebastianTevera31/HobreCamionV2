package com.rfz.appflotal.data.repository.disassembly

import com.rfz.appflotal.data.model.catalogo.TipoDatoOffline
import com.rfz.appflotal.data.model.catalogo.aCatalogo
import com.rfz.appflotal.data.model.catalogo.aCausaRespuesta
import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.network.service.catalogo.LocalCatalogosDataSource
import com.rfz.appflotal.data.network.service.disassembly.RemoteDisassemblyTireDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DisassemblyTireRepository @Inject constructor(
    private val disassemblyCauseDataSource: RemoteDisassemblyTireDataSource,
    private val getTasksUseCase: GetTasksUseCase,
    private val local: LocalCatalogosDataSource,
) {

    /** Del servidor (y se guarda); sin red, lo guardado. */
    suspend fun doDisassemblyCause(): Result<List<DisassemblyCauseResponse>> {
        val remoto = pedirAlServidor()
        remoto.onSuccess { guardar(it) }
        if (remoto.isSuccess) return remoto
        val guardadas = local.catalogo(TipoDatoOffline.CAUSA_DESMONTAJE)
        return if (guardadas.isNotEmpty()) Result.success(guardadas.map { it.aCausaRespuesta() })
        else remoto
    }

    suspend fun actualizar(): Boolean = pedirAlServidor().onSuccess { guardar(it) }.isSuccess

    private suspend fun guardar(causas: List<DisassemblyCauseResponse>) =
        local.guardarCatalogo(TipoDatoOffline.CAUSA_DESMONTAJE, causas.map { it.aCatalogo() })

    private suspend fun pedirAlServidor(): Result<List<DisassemblyCauseResponse>> = runCatching {
        getTasksUseCase().first().first().fld_token
    }.fold(
        onSuccess = { token -> disassemblyCauseDataSource.doDisassemblyCause(token) },
        onFailure = { Result.failure(it) }
    )
}
