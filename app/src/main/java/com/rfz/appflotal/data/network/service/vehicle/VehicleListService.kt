package com.rfz.appflotal.data.network.service.vehicle

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import com.rfz.appflotal.data.network.client.tire.TireGetClient

import com.rfz.appflotal.data.network.client.vehicle.VehicleListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class VehicleListService @Inject constructor(private val vehicleListClient: VehicleListClient) {
    suspend fun doVehicleList(tok:String): Response<List<VehicleListResponse>> {
        return withContext(Dispatchers.IO) {
            vehicleListClient.doVehicleList(tok)
        }
    }
}