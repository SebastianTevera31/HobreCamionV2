package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.network.client.tire.DisassemblyTireCrudClient
import com.rfz.appflotal.data.network.client.tire.InspectionTireCrudClient
import com.rfz.appflotal.data.network.client.tire.TireCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class InspectionTireCrudService @Inject constructor(private val inspectionTireCrudClient: InspectionTireCrudClient) {
    suspend fun doInspectionTire(requestBody: InspectionTireDto, tok:String): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            inspectionTireCrudClient.doInspectionTire(requestBody,tok)
        }
    }
}