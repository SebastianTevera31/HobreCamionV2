package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.tire.RepairedTireDto
import com.rfz.appflotal.data.model.tire.RetreatedTireDto
import com.rfz.appflotal.data.network.client.tire.TireService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteTireDataSource @Inject constructor(private val tireService: TireService) {
    suspend fun postRetreatedTire(token: String, body: RetreatedTireDto) = networkRequestHelper {
        tireService.postRetreatedTire("Bearer $token", body)
    }

    suspend fun postRepairedTire(token: String, body: RepairedTireDto) = networkRequestHelper {
        tireService.postRepairedTire("Bearer $token", body)
    }
}