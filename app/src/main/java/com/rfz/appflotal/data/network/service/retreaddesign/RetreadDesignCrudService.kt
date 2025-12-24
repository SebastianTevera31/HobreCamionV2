package com.rfz.appflotal.data.network.service.retreaddesign

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignCrudClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadDesignCrudService @Inject constructor(
    private val retreadDesignCrudClient: RetreadDesignCrudClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doCrudRetreadDesign(
        requestBody: RetreadDesignCrudDto,
    ): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase.invoke().first()[0].fld_token
            retreadDesignCrudClient.doCrudRetreadDesign("bearer $token", requestBody)
        }
    }
}