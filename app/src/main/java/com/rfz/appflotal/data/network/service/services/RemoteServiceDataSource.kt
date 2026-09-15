package com.rfz.appflotal.data.network.service.services

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.model.services.dto.ServiceOrderDto
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import com.rfz.appflotal.data.network.client.services.ServiceClient
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteServiceDataSource @Inject constructor(
    private val serviceClient: ServiceClient
) {
    suspend fun getServices(): Result<List<ServiceResponseDto>> {
        return networkRequestHelper {
            serviceClient.getServices()
        }
    }

    suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto,
        token: String
    ): Result<GeneralResponse> {
        return networkRequestHelper {
            serviceClient.doCrudServiceDetail(requestBody, token)
        }
    }

    suspend fun doGetServiceType(
        token: String
    ): Result<TypeServiceDto> {
        return networkRequestHelper {
            serviceClient.doGetServiceType(token)
        }
    }
}
