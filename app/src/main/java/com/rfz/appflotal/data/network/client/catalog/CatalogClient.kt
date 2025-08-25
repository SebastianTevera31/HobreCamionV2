package com.rfz.appflotal.data.network.client.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import retrofit2.Response
import retrofit2.http.GET

interface CatalogClient {
    @GET("api/Catalog/Country")
    suspend fun getCountries(): Response<List<GetCountriesResponse>>

    @GET("api/Catalog/Sector")
    suspend fun getSectors(): Response<List<GetSectorsResponse>?>
}
