package com.rfz.appflotal.data.network.service.utilization

import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.data.network.client.scrap.ScrapReportClient
import com.rfz.appflotal.data.network.client.utilization.UtilizationClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class UtilizationService @Inject constructor(private val utilizationClient: UtilizationClient) {
    suspend fun doUtilization(tok:String): Response<List<UtilizationResponse>> {
        return withContext(Dispatchers.IO) {
            utilizationClient.doUtilization(tok)
        }
    }
}