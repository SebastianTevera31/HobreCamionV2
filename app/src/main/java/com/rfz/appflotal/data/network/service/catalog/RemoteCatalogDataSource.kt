package com.rfz.appflotal.data.network.service.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.network.client.catalog.CatalogClient
import com.rfz.appflotal.data.network.networkRequestHelper
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RemoteCatalogDataSource @Inject constructor(
    private val catalogClient: CatalogClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    /**
     * Cabecera Bearer del usuario en sesión, o null si no hay sesión (p. ej. en el registro).
     * El servidor decide el idioma de los catálogos según el token, por eso se envía siempre
     * que exista.
     */
    private suspend fun bearerOrNull(): String? =
        getTasksUseCase().first().firstOrNull()?.fld_token
            ?.takeIf { it.isNotBlank() }
            ?.let { "Bearer $it" }

    suspend fun getCountries(): ApiResult<List<GetCountriesResponse>?> {
        return requestHelper("GetCountries") {
            catalogClient.getCountries(bearerOrNull())
        }
    }

    suspend fun getSectors(): ApiResult<List<GetSectorsResponse>?> {
        return requestHelper("GetSectors") {
            catalogClient.getSectors(bearerOrNull())
        }
    }

    suspend fun getTireInspectionReport(): ApiResult<List<GetTireInspectionReportResponse>?> {
        return requestHelper("GetTireInspectionReportType") {
            val token = getTasksUseCase().first()[0].fld_token
            catalogClient.getTireInspectionReport("bearer $token")
        }
    }

    suspend fun getStates(token: String, countryId: Int) = networkRequestHelper {
        catalogClient.getStates("Bearer $token", countryId)
    }
}