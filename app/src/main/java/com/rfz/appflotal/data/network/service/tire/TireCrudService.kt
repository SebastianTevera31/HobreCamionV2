package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.network.client.tire.TireCrudClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireCrudService @Inject constructor(
    private val tireCrudClient: TireCrudClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doTireCrud(requestBody: TireCrudDto, tok: String): Response<GeneralResponse> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase.invoke().first()[0].fld_token
            tireCrudClient.doTireCrud("bearer $token", requestBody)
        }
    }
}