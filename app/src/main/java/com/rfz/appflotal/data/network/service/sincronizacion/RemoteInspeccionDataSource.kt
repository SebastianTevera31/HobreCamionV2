package com.rfz.appflotal.data.network.service.sincronizacion

import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.network.client.tire.InspectionTireCrudClient
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteInspeccionDataSource @Inject constructor(
    private val inspeccionClient: InspectionTireCrudClient,
) {
    suspend fun enviarInspeccion(token: String, inspeccion: InspectionTireDto) =
        networkRequestHelper { inspeccionClient.doInspectionTire("Bearer $token", inspeccion) }
}
