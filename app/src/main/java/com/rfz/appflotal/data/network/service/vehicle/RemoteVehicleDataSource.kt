package com.rfz.appflotal.data.network.service.vehicle

import com.rfz.appflotal.data.network.client.vehicle.VehicleService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteVehicleDataSource @Inject constructor(private val vehicleService: VehicleService) {
    suspend fun fetchLastService(token: String) = networkRequestHelper {
        vehicleService.fetchLastOdometer("Bearer $token")
    }
}