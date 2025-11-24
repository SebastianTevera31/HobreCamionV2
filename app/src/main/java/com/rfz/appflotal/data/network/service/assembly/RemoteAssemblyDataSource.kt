package com.rfz.appflotal.data.network.service.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.network.client.assembly.AssemblyTireService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RemoteAssemblyDataSource @Inject constructor(
    private val assemblyTireService: AssemblyTireService,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun registerAssemblyTire(
        assemblyTire: AssemblyTireDto
    ): Response<Unit> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase().first()[0].fld_token
            assemblyTireService.registerAssemblyTire("Bearer $token", assemblyTire)
        }
    }
}