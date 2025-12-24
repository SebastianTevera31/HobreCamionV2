package com.rfz.appflotal.data.network.service.vehicle

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import com.rfz.appflotal.data.network.client.vehicle.VehicleCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class VehicleCrudService @Inject constructor(private val vehicleCrudClient: VehicleCrudClient) {
    suspend fun doCrudVehicle(requestBody: VehicleCrudDto, tok:String): Response<GeneralResponse> {
        return withContext(Dispatchers.IO) {
            vehicleCrudClient.doCrudVehicle(requestBody,tok)
        }
    }
}