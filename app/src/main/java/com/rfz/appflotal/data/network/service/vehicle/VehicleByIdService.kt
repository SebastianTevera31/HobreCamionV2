package com.rfz.appflotal.data.network.service.vehicle

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.vehicle.response.VehicleIdResponse
import com.rfz.appflotal.data.network.client.tire.TireGetClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleByIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class VehicleByIdService @Inject constructor(private val vehicleByIdClient: VehicleByIdClient) {
    suspend fun doGetVehicleById(tok:String,id_vehicle: Int): Response<List<VehicleIdResponse>> {
        return withContext(Dispatchers.IO) {
            vehicleByIdClient.doGetVehicleById(tok,id_vehicle)
        }
    }
}