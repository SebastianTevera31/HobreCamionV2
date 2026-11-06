package com.rfz.appflotal.data.network.client.services

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ServiceClient {
    @GET("api/Service/GetServices")
    suspend fun getServices(
        @Header("Authorization") token: String
    ): Response<List<ServiceResponseDto>>

    @Multipart
    @POST("api/Service/CrudService")
    suspend fun doCrudServiceDetail(
        @Header("Authorization") token: String,
        @Part("id_service") idService: RequestBody,
        @Part("fld_description") description: RequestBody,
        @Part("id_serviceType") idServiceType: RequestBody,
        @Part("fld_price") price: RequestBody,
        @Part("fld_date") date: RequestBody,
        @Part("fld_provider") provider: RequestBody,
        @Part("fld_cant") quantity: RequestBody,
        @Part("p_vehicle_fk_1") vehicleId: RequestBody,
        @Part("fld_notes") notes: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<GeneralResponse>

    @GET("api/Service/DeleteService")
    suspend fun deleteService(
        @Header("Authorization") token: String,
        @Query("id_service") idService: Int
    ): Response<List<GeneralResponse>>

    @GET("api/Service/GetTypeService")
    suspend fun doGetServiceType(
        @Header("Authorization") token: String
    ): Response<List<TypeServiceDto>>
}
