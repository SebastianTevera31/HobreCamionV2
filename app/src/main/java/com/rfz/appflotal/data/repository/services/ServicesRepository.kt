package com.rfz.appflotal.data.repository.services

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import com.rfz.appflotal.data.network.service.services.RemoteServiceDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface ServicesRepository {
    suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto,
    ): Result<GeneralResponse>

    suspend fun doGetServiceType(
    ): Result<List<TypeServiceDto>>

    suspend fun getServices(): Result<List<ServiceResponseDto>?>
}

class ServicesRepositoryImp @Inject constructor(
    private val remoteServiceDataSource: RemoteServiceDataSource,
    private val getTasksUseCase: GetTasksUseCase
) : ServicesRepository {

    override suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto
    ): Result<GeneralResponse> {
        val user = getTasksUseCase().first()
        if (user.isEmpty()) return Result.failure(Exception("No hay usuario logueado"))
        val token = user.first().fld_token
        return remoteServiceDataSource.doCrudServiceDetail(requestBody, token)
    }

    override suspend fun doGetServiceType(
    ): Result<List<TypeServiceDto>> {
        val user = getTasksUseCase().first()
        if (user.isEmpty()) return Result.failure(Exception("No hay usuario logueado"))
        val token = user.first().fld_token
        return remoteServiceDataSource.doGetServiceType(token)
    }

    override suspend fun getServices(): Result<List<ServiceResponseDto>?> {
        val user = getTasksUseCase().first()
        if (user.isEmpty()) return Result.failure(Exception("No hay usuario logueado"))
        val token = user.first().fld_token
        return remoteServiceDataSource.getServices(token)
    }

}


