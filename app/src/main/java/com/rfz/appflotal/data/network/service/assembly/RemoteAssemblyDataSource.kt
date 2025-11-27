package com.rfz.appflotal.data.network.service.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.model.assembly.AssemblyTireResponse
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.client.assembly.AssemblyTireService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RemoteAssemblyDataSource @Inject constructor(
    private val assemblyTireService: AssemblyTireService,
) {
    suspend fun pushAssemblyTire(
        token: String,
        assemblyTire: AssemblyTireDto
    ): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            assemblyTireService.createAssemblyTire("Bearer $token", assemblyTire)
        }
    }

    suspend fun fetchMountedTire(
        token: String,
        monitorId: Int
    ): Response<List<AssemblyTireResponse>> {
        return withContext(Dispatchers.IO) {
            assemblyTireService.getMountedTires(
                token = "Bearer $token",
                idMonitor = monitorId
            )
        }
    }
}