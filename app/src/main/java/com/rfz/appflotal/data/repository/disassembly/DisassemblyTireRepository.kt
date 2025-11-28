package com.rfz.appflotal.data.repository.disassembly

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.disassembly.tire.toDto
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.data.network.service.disassembly.RemoteDisassemblyTireDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DisassemblyTireRepository @Inject constructor(
    private val disassemblyCauseDataSource: RemoteDisassemblyTireDataSource,
    private val localAssemblyTireDataSource: LocalAssemblyDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun doDisassemblyCause(): Result<List<DisassemblyCauseResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return disassemblyCauseDataSource.doDisassemblyCause(token)
    }

    suspend fun pushDisassemblyTire(request: DisassemblyTire) {
        val token = getTasksUseCase().first().first().fld_token
        disassemblyCauseDataSource.createDisassemblyTire(token, request.toDto()).onSuccess {
            localAssemblyTireDataSource.deleteAssemblyTire(request.positionTire)
        }
    }
}