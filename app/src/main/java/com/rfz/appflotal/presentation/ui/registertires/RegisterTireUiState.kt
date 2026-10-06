package com.rfz.appflotal.presentation.ui.registertires

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.axle.Axle
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OdometerValidation
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

private data class CatalogEntry(override val id: Int, override val description: String) : CatalogItem

fun AcquisitionTypeResponse.localizedDescription(language: String): String =
    if (language == "en" && enDescription.isNotBlank()) enDescription else description

fun AcquisitionTypeResponse.asCatalogItem(language: String): CatalogItem =
    CatalogEntry(idAcquisitionType, localizedDescription(language))
fun ProductResponse.asCatalogItem(): CatalogItem = CatalogEntry(idProduct, descriptionProduct)

data class RegisterTireUiState(
    val positionTire: String = "",
    val acquisitionTypes: List<AcquisitionTypeResponse> = emptyList(),
    val products: List<ProductResponse> = emptyList(),
    val axleList: List<Axle> = emptyList(),
    val selectedAcquisitionType: AcquisitionTypeResponse? = null,
    val selectedProduct: ProductResponse? = null,
    val selectedAxle: Axle? = null,
    val acquisitionDate: String = "",
    val cost: String = "",
    val folioFactura: String = "",
    val treadDepth: String = "",
    val tireNumber: String = "",
    val dot: String = "",
    val odometer: String = "",
    val currentOdometer: String = "0",
    val odometerUnit: UnidadOdometro = UnidadOdometro.KILOMETROS,
    val isOdometerValid: OdometerValidation = OdometerValidation.EMPTY,
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null,
    val errorMessage: String? = null,
)
