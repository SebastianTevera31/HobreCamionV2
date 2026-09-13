package com.rfz.appflotal.data.repository.services

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.model.services.dto.ServiceOrderDto
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.network.service.services.RemoteServiceDataSource
import javax.inject.Inject

interface ServicesRepository {
    suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto,
        token: String
    ): Result<GeneralResponse>

    suspend fun doCrudServiceOrder(
        requestBody: ServiceOrderDto,
        token: String
    ): Result<GeneralResponse>

    suspend fun getServices(): Result<List<ServiceResponseDto>?>
}

class ServicesRepositoryImp @Inject constructor(
    private val remoteServiceDataSource: RemoteServiceDataSource
) : ServicesRepository {

    override suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto,
        token: String
    ): Result<GeneralResponse> {
        return remoteServiceDataSource.doCrudServiceDetail(requestBody, token)
    }

    override suspend fun doCrudServiceOrder(
        requestBody: ServiceOrderDto,
        token: String
    ): Result<GeneralResponse> {
        return remoteServiceDataSource.doCrudServiceOrder(requestBody, token)
    }

    override suspend fun getServices(): Result<List<ServiceResponseDto>?> {
        return remoteServiceDataSource.getServices()
    }

}


