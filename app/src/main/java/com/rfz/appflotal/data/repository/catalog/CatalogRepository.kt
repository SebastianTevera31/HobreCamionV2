package com.rfz.appflotal.data.repository.catalog

import com.rfz.appflotal.data.dao.InspectionCatalogDao
import com.rfz.appflotal.data.model.tire.toEntity
import com.rfz.appflotal.data.model.tire.toResponse
import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.catalog.CatalogService
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val catalogService: CatalogService,
    private val inspectionCatalogDao: InspectionCatalogDao
) {

    suspend fun onGetCountries(): ApiResult<List<GetCountriesResponse>?> {
        return catalogService.getCountries()
    }

    suspend fun onGetSectors(): ApiResult<List<GetSectorsResponse>?> {
        return catalogService.getSectors()
    }

    suspend fun onGetTireReports(): ApiResult<List<GetTireInspectionReportResponse>?> {
        val local = inspectionCatalogDao.getActiveCatalog()
        return if (local.isNotEmpty()) {
            ApiResult.Success(local.map { it.toResponse() })
        } else {
            val remote = catalogService.getTireInspectionReport()
            if (remote is ApiResult.Success) {
                remote.data?.let { list ->
                    inspectionCatalogDao.upsertCatalog(list.map { it.toEntity() })
                }
            }
            remote
        }
    }

    suspend fun refreshTireReports() {
        val remote = catalogService.getTireInspectionReport()
        if (remote is ApiResult.Success) {
            remote.data?.let { list ->
                inspectionCatalogDao.upsertCatalog(list.map { it.toEntity() })
            }
        }
    }
}