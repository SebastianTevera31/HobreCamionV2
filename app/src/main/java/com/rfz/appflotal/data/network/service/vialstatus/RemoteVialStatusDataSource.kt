package com.rfz.appflotal.data.network.service.vialstatus

import com.rfz.appflotal.data.network.client.vialstatus.VialStatusService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteVialStatusDataSource @Inject constructor(private val vialStatusService: VialStatusService) {

    suspend fun getMapByState(id: Int) = networkRequestHelper {
        vialStatusService.getRoadMap(id)
    }
}