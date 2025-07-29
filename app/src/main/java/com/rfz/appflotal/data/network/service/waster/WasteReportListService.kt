package com.rfz.appflotal.data.network.service.waster

import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import com.rfz.appflotal.data.network.client.vehicle.VehicleListClient
import com.rfz.appflotal.data.network.client.waster.WasteReportListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class WasteReportListService @Inject constructor(private val wasteReportListClient: WasteReportListClient) {
    suspend fun doWasteReportList(tok:String): Response<List<WasteReportListResponse>> {
        return withContext(Dispatchers.IO) {
            wasteReportListClient.doWasteReportList(tok)
        }
    }
}