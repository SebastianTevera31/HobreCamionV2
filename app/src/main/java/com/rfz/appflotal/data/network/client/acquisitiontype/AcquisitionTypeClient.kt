package com.rfz.appflotal.data.network.client.acquisitiontype

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


interface AcquisitionTypeClient {
    @GET("api/Catalog/AcquisitionType")
    suspend fun doAcquisitionType(@Header("Authorization") token: String): Response<List<AcquisitionTypeResponse>>
}
