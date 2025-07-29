package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.data.network.client.tire.TireGetClient
import com.rfz.appflotal.data.network.client.tire.TireListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireListService @Inject constructor(private val tireListClient: TireListClient) {
    suspend fun doTireList(tok:String): Response<List<TireListResponse>> {
        return withContext(Dispatchers.IO) {
            tireListClient.TireList(tok)
        }
    }
}