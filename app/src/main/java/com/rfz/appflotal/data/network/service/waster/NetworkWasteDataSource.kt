package com.rfz.appflotal.data.network.service.waster

import com.rfz.appflotal.data.network.client.waster.WasteService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject


class NetworkWasteDataSource @Inject constructor(private val wasteService: WasteService) {
    suspend fun doWasteReportList(tok: String) = networkRequestHelper {
        wasteService.doWasteReportList("Bearer $tok")
    }

    suspend fun pushTireToScrap(tok: String) = networkRequestHelper {
        wasteService.postTireToScrap("Bearer $tok")
    }
}