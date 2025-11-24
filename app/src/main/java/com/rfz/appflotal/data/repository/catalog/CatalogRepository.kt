package com.rfz.appflotal.data.repository.catalog

import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.catalog.CatalogService
import javax.inject.Inject

class CatalogRepository @Inject constructor(private val catalogService: CatalogService) {

    suspend fun onGetCountries(): ApiResult<List<GetCountriesResponse>?> {
        return catalogService.getCountries()
    }

    suspend fun onGetSectors(): ApiResult<List<GetSectorsResponse>?> {
        return catalogService.getSectors()
    }

    suspend fun onGetTireReports(): ApiResult<List<GetTireInspectionReportResponse>?> {
        return catalogService.getTireInspectionReport()
    }
}