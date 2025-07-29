package com.rfz.appflotal.data.network.service.scrap

import com.rfz.appflotal.data.model.route.response.RouteResponse
import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import com.rfz.appflotal.data.network.client.route.RouteClient
import com.rfz.appflotal.data.network.client.scrap.ScrapReportClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class ScrapReportService @Inject constructor(private val scrapReportClient: ScrapReportClient) {
    suspend fun doScrapReport(tok:String): Response<List<ScrapReportResponse>> {
        return withContext(Dispatchers.IO) {
            scrapReportClient.doScrapReport(tok)
        }
    }
}