package com.rfz.appflotal.data.repository.disassembly

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTireEntity
import com.rfz.appflotal.data.network.service.assembly.AssemblySyncScheduler
import com.rfz.appflotal.data.network.service.disassembly.LocalDisassemblyDataSource
import com.rfz.appflotal.data.network.service.disassembly.RemoteDisassemblyTireDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DisassemblyTireRepository @Inject constructor(
    private val disassemblyCauseDataSource: RemoteDisassemblyTireDataSource,
    private val localDisassemblyDataSource: LocalDisassemblyDataSource,
    private val getTasksUseCase: GetTasksUseCase,
    private val syncScheduler: AssemblySyncScheduler
) {

    suspend fun doDisassemblyCause(): Result<List<DisassemblyCauseResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return disassemblyCauseDataSource.doDisassemblyCause(token)
    }

    suspend fun pushDisassemblyTire(request: DisassemblyTire) {
        val token = getTasksUseCase().first().first().fld_token
        
        // Guardado Local
        val entity = DisassemblyTireEntity(
            positionTire = request.positionTire,
            disassemblyCause = request.disassemblyCause,
            destination = request.destination,
            dateOperation = request.dateOperation,
            odometer = request.odometer,
            updatedAt = System.currentTimeMillis()
        )
        
        localDisassemblyDataSource.saveDisassemblyTire(entity)
        
        // Encolar Sincronización
        syncScheduler.enqueueDisassembly(request.positionTire, token)
    }
}