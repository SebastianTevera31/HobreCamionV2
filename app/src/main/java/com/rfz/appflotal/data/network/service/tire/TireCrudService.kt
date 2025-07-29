package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.network.client.tire.DisassemblyTireCrudClient
import com.rfz.appflotal.data.network.client.tire.TireCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireCrudService @Inject constructor(private val tireCrudClient: TireCrudClient) {
    suspend fun doTireCrud(requestBody: TireCrudDto, tok:String): Response<MessageResponse> {
        return withContext(Dispatchers.IO) {
            tireCrudClient.doTireCrud(requestBody,tok)
        }
    }
}