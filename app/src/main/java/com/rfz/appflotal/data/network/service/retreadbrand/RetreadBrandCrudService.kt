package com.rfz.appflotal.data.network.service.retreadbrand

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import com.rfz.appflotal.data.network.client.retreaband.RetreadBrandCrudClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadBrandCrudService @Inject constructor(
    private val retreadBrandCrudClient: RetreadBrandCrudClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doRetreadBrand(
        requestBody: RetreadBrandDto,
    ): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase().first()[0].fld_token
            retreadBrandCrudClient.doRetreadBrand("bearer $token", requestBody)
        }
    }
}