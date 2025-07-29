package com.rfz.appflotal.data.network.service.disassembly

import com.rfz.appflotal.data.model.destination.response.DestinationResponse
import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.network.client.destination.DestinationClient
import com.rfz.appflotal.data.network.client.disassembly.DisassemblyCauseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class DisassemblyCauseService @Inject constructor(private val disassemblyCauseClient: DisassemblyCauseClient) {
    suspend fun doDisassemblyCause(tok:String): Response<List<DisassemblyCauseResponse>> {
        return withContext(Dispatchers.IO) {
            disassemblyCauseClient.doDisassemblyCause(tok)
        }
    }
}