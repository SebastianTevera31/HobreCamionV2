package com.rfz.appflotal.data.repository.disassembly

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.network.service.disassembly.DisassemblyCauseService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DisassemblyCauseRepository @Inject constructor(
    private val disassemblyCauseService: DisassemblyCauseService,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun doDisassemblyCause(): Result<List<DisassemblyCauseResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return disassemblyCauseService.doDisassemblyCause(token)
    }
}