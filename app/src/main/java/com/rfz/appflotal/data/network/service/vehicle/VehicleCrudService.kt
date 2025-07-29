package com.rfz.appflotal.data.network.service.vehicle

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import com.rfz.appflotal.data.network.client.tire.DisassemblyTireCrudClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class VehicleCrudService @Inject constructor(private val vehicleCrudClient: VehicleCrudClient) {
    suspend fun doCrudVehicle(requestBody: VehicleCrudDto, tok:String): Response<MessageResponse> {
        return withContext(Dispatchers.IO) {
            vehicleCrudClient.doCrudVehicle(requestBody,tok)
        }
    }
}