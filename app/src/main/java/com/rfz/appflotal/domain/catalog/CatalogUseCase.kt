package com.rfz.appflotal.domain.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.catalog.CatalogRepository
import javax.inject.Inject

class CatalogUseCase @Inject constructor(private val catalogRepository: CatalogRepository) {
    suspend fun onGetCountries(): ApiResult<List<GetCountriesResponse>?> {
        return catalogRepository.onGetCountries()
    }

    suspend fun onGetSectors(): ApiResult<List<GetSectorsResponse>?> {
        return catalogRepository.onGetSectors()
    }
}