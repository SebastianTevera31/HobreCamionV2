package com.rfz.appflotal.data.network.client.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import com.rfz.appflotal.data.model.catalog.StateDto
import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CatalogClient {
    // El token (opcional) lleva el idioma del usuario; sin sesión (registro) se omite.
    @GET("api/Catalog/Country")
    suspend fun getCountries(
        @Header("Authorization") token: String? = null,
        @Query("language") language: String?
    ): Response<List<GetCountriesResponse>>

    @GET("api/Catalog/Sector")
    suspend fun getSectors(
        @Header("Authorization") token: String? = null,
        @Query("language") language: String?
    ): Response<List<GetSectorsResponse>?>

    @GET("api/Catalog/TireInspectionReport")
    suspend fun getTireInspectionReport(@Header("Authorization") token: String): Response<List<GetTireInspectionReportResponse>?>

    @GET("api/Catalog/States")
    suspend fun getStates(
        @Header("Authorization") token: String,
        @Query("id_country") countryId: Int
    ): Response<List<StateDto>>

    // Recordatorios de mantenimiento; el servidor decide el idioma del texto según el token.
    @GET("api/Catalog/Notifications")
    suspend fun getMaintenanceNotifications(
        @Header("Authorization") token: String? = null
    ): Response<List<MaintenanceNotificationResponse>>

    // Sin parámetros; el token (opcional) se envía cuando hay sesión.
    @GET("api/Catalog/TopTenAccesories")
    suspend fun getTopTenAccessories(
        @Header("Authorization") token: String? = null
    ): Response<List<TopTenAccessoryResponse>>
}
