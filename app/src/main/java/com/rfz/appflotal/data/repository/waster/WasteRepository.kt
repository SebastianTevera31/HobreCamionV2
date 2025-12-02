package com.rfz.appflotal.data.repository.waster

import com.rfz.appflotal.data.model.waster.ScrapTirePile
import com.rfz.appflotal.data.model.waster.ScrapTirePileDto
import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import com.rfz.appflotal.data.model.waster.toDto
import com.rfz.appflotal.data.network.service.waster.NetworkWasteDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class WasteRepository @Inject constructor(
    private val wasteService: NetworkWasteDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun doWasteReportList(): Result<List<WasteReportListResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return wasteService.doWasteReportList(token)
    }

    suspend fun sendTireToScrap(response: ScrapTirePile) {
        val token = getTasksUseCase().first().first().fld_token
        wasteService.pushTireToScrap(token, response.toDto())
    }
}