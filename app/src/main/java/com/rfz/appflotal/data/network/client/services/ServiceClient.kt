package com.rfz.appflotal.data.network.client.services

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.model.services.dto.ServiceOrderDto
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ServiceClient {
    @GET("api/Service/GetServices")
    suspend fun getServices(): Response<List<ServiceResponseDto>>

    @POST("api/ServiceDetail/CrudService")
    suspend fun doCrudServiceDetail(
        @Body requestBody: ServiceDetailDto,
        @Header("Authorization") token: String
    ): Response<GeneralResponse>

    @GET("api/ServiceOrder/GetTypeService")
    suspend fun doGetServiceType(
        @Header("Authorization") token: String
    ): Response<TypeServiceDto>
}
