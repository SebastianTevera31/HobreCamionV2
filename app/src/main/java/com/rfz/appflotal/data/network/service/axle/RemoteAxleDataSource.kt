package com.rfz.appflotal.data.network.service.axle

import com.rfz.appflotal.data.model.axle.GetAxleResponseDto
import com.rfz.appflotal.data.network.client.axle.AxleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteAxleDataSource @Inject constructor(private val axleService: AxleService) {
    suspend fun fetchAxleList(token: String): List<GetAxleResponseDto> {
        return withContext(Dispatchers.IO) {
            axleService.getAxleList("Bearer $token")
        }
    }
}