package com.rfz.appflotal.data.network.client.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.model.catalog.StateDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CatalogClient {
    @GET("api/Catalog/Country")
    suspend fun getCountries(): Response<List<GetCountriesResponse>>

    @GET("api/Catalog/Sector")
    suspend fun getSectors(): Response<List<GetSectorsResponse>?>

    @GET("api/Catalog/TireInspectionReport")
    suspend fun getTireInspectionReport(@Header("Authorization") token: String): Response<List<GetTireInspectionReportResponse>?>

    @GET("api/Catalog/States")
    suspend fun getStates(
        @Header("Authorization") token: String,
        @Query("id_country") countryId: Int
    ): Response<List<StateDto>>
}
