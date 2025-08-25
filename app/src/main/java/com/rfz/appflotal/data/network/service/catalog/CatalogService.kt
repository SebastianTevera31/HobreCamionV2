package com.rfz.appflotal.data.network.service.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.network.client.catalog.CatalogClient
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CatalogService @Inject constructor(
    private val catalogClient: CatalogClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun getCountries(): ApiResult<List<GetCountriesResponse>?> {
        return requestHelper("GetCountries") {
            catalogClient.getCountries()
        }
    }

    suspend fun getSectors(): ApiResult<List<GetSectorsResponse>?> {
        return requestHelper("GetSectors") {
            catalogClient.getSectors()
        }
    }
}