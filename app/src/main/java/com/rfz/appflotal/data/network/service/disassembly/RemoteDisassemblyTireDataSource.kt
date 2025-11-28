package com.rfz.appflotal.data.network.service.disassembly

import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTireRequestDto
import com.rfz.appflotal.data.network.client.disassembly.DisassemblyCauseService
import com.rfz.appflotal.data.network.networkRequestHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RemoteDisassemblyTireDataSource @Inject constructor(private val disassemblyCauseClient: DisassemblyCauseService) {
    suspend fun doDisassemblyCause(tok: String) = networkRequestHelper {
        withContext(Dispatchers.IO) {
            disassemblyCauseClient.doDisassemblyCause("Bearer $tok")
        }
    }

    suspend fun createDisassemblyTire(tok: String, request: DisassemblyTireRequestDto) =
        networkRequestHelper {
            disassemblyCauseClient.createDisassemblyTire("Bearer $tok", request)
        }
}