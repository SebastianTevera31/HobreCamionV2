package com.rfz.appflotal.data.repository.waster

import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import com.rfz.appflotal.data.network.service.DataError
import com.rfz.appflotal.data.network.service.waster.WasteReportListService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class WasteReportListRepository @Inject constructor(
    private val wasteReportListService: WasteReportListService,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun doWasteReportList(): Result<List<WasteReportListResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return wasteReportListService.doWasteReportList(token)
    }
}