package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.network.client.tire.InspectionTireCrudClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class InspectionTireCrudService @Inject constructor(
    private val inspectionTireCrudClient: InspectionTireCrudClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doInspectionTire(requestBody: InspectionTireDto): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase().first()[0].fld_token
            inspectionTireCrudClient.doInspectionTire("bearer $token", requestBody)
        }
    }
}