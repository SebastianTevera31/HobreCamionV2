package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.model.tire.dto.TireSizeDto
import com.rfz.appflotal.data.network.client.tire.InspectionTireCrudClient
import com.rfz.appflotal.data.network.client.tire.TireSizeCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireSizeCrudService @Inject constructor(private val tireSizeCrudClient: TireSizeCrudClient) {
    suspend fun doCrudTireSize(requestBody: TireSizeDto, tok:String): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            tireSizeCrudClient.doCrudTireSize(requestBody,tok)
        }
    }
}