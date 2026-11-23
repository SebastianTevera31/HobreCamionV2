package com.rfz.appflotal.data.repository.catalog

import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.catalogo.TipoDatoOffline
import com.rfz.appflotal.data.model.catalogo.aCatalogo
import com.rfz.appflotal.data.model.catalogo.aReporteRespuesta
import com.rfz.appflotal.data.network.service.catalogo.LocalCatalogosDataSource
import kotlinx.coroutines.flow.Flow
import com.rfz.appflotal.data.model.catalog.FuelTypeItem
import com.rfz.appflotal.data.model.catalog.GetCountriesResponse
import com.rfz.appflotal.data.model.catalog.GetSectorsResponse
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.catalog.RemoteCatalogDataSource
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val catalogService: RemoteCatalogDataSource,
    private val local: LocalCatalogosDataSource,
) {

    suspend fun onGetCountries(): ApiResult<List<GetCountriesResponse>?> {
        return catalogService.getCountries(language = AppLocale.currentLocale.value.language)
    }

    suspend fun onGetSectors(): ApiResult<List<GetSectorsResponse>?> {
        return catalogService.getSectors(language = AppLocale.currentLocale.value.language)
    }

    /** Primero lo guardado (cambia poco); si no hay, se pide al servidor y se guarda. */
    suspend fun onGetTireReports(): ApiResult<List<GetTireInspectionReportResponse>?> {
        val guardados = local.catalogo(TipoDatoOffline.REPORTE_INSPECCION).filter { it.activo }
        return if (guardados.isNotEmpty()) {
            ApiResult.Success(guardados.map { it.aReporteRespuesta() })
        } else {
            val remote = catalogService.getTireInspectionReport()
            if (remote is ApiResult.Success) remote.data?.let { guardarReportes(it) }
            remote
        }
    }

    suspend fun clearTireReports() {
        local.borrarCatalogo(TipoDatoOffline.REPORTE_INSPECCION)
    }

    /** false si el servidor no respondió. */
    suspend fun refreshTireReports(): Boolean {
        val remote = catalogService.getTireInspectionReport()
        if (remote is ApiResult.Success) remote.data?.let { guardarReportes(it) }
        return remote is ApiResult.Success
    }

    /** Cuándo se descargó cada catálogo que se usa sin conexión. */
    fun observarDescargas(): Flow<Map<TipoDatoOffline, Long>> = local.observarDescargas()

    private suspend fun guardarReportes(reportes: List<GetTireInspectionReportResponse>) =
        local.guardarCatalogo(TipoDatoOffline.REPORTE_INSPECCION, reportes.map { it.aCatalogo() })

    /** Tipos de combustible, en el orden en que los entrega el servidor. */
    suspend fun getFuelTypes(): Result<List<FuelTypeItem>> =
        catalogService.getFuelTypes().map { list -> list.map { it.toCatalog() } }
}