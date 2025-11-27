package com.rfz.appflotal.data.network.service.axle

import com.rfz.appflotal.data.network.client.axle.AxleService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteAxleDataSource @Inject constructor(private val axleService: AxleService) {
    suspend fun fetchAxleList(token: String) = networkRequestHelper {
        axleService.getAxleList("Bearer $token")
    }
}