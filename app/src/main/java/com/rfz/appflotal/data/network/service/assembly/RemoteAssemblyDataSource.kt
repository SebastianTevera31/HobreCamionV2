package com.rfz.appflotal.data.network.service.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.network.client.assembly.AssemblyTireService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject


class RemoteAssemblyDataSource @Inject constructor(
    private val assemblyTireService: AssemblyTireService,
) {
    suspend fun pushAssemblyTire(
        token: String,
        assemblyTire: AssemblyTireDto
    ) = networkRequestHelper {
        assemblyTireService.createAssemblyTire(
            "Bearer $token",
            assemblyTire
        )
    }

    suspend fun fetchMountedTire(
        token: String,
        monitorId: Int
    ) = networkRequestHelper {
        assemblyTireService.getMountedTires(
            token = "Bearer $token",
            idMonitor = monitorId
        )
    }
}