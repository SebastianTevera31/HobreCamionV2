package com.rfz.appflotal.data.network.service.vehicle

import com.rfz.appflotal.data.model.vehicle.response.TypeVehicleResponse
import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import com.rfz.appflotal.data.network.client.vehicle.VehicleListClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleTypeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class VehicleTypeService @Inject constructor(private val vehicleTypeClient: VehicleTypeClient) {
    suspend fun doTypeVehicle(tok:String): Response<List<TypeVehicleResponse>> {
        return withContext(Dispatchers.IO) {
            vehicleTypeClient.doTypeVehicle(tok)
        }
    }
}