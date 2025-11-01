package com.rfz.appflotal.data.network.service.utilization

import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.data.network.client.scrap.ScrapReportClient
import com.rfz.appflotal.data.network.client.utilization.UtilizationClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class UtilizationService @Inject constructor(
    private val utilizationClient: UtilizationClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doUtilization(): Response<List<UtilizationResponse>> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase.invoke().first()[0].fld_token
            utilizationClient.doUtilization("bearer $token")
        }
    }
}