package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import com.rfz.appflotal.data.network.client.tire.DisassemblyTireCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class DisassemblyTireCrudService @Inject constructor(private val disassemblyTireCrudClient: DisassemblyTireCrudClient) {
    suspend fun doDisassemblyTire(requestBody: DisassemblyTireDto, tok:String): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) {
            disassemblyTireCrudClient.doDisassemblyTire(requestBody,tok)
        }
    }
}