package com.rfz.appflotal.presentation.ui.mountedtire.model

import androidx.annotation.StringRes
import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

/** Datos del montaje que se muestran como referencia (no editables). */
data class MountedTireSummary(
    val axleDescription: String?,
    val assemblyDate: String,
    val odometer: Int,
    val odometerUnit: UnidadOdometro,
)

data class EditMountedTireUiState(
    val position: String = "",
    val loadStatus: OperationStatus = OperationStatus.Loading,
    val summary: MountedTireSummary? = null,
    val acquisitionTypes: List<AcquisitionTypeResponse> = emptyList(),
    val products: List<ProductResponse> = emptyList(),
    val selectedAcquisitionType: AcquisitionTypeResponse? = null,
    val selectedProduct: ProductResponse? = null,
    val acquisitionDate: String = "",
    val folioFactura: String = "",
    val cost: String = "",
    val treadDepth: String = "",
    val tireNumber: String = "",
    val dot: String = "",
    val saveStatus: OperationStatus = OperationStatus.Idle,
    @param:StringRes val errorRes: Int? = null,
    val errorMessage: String? = null,
) {
    val isDotTooLong: Boolean get() = dot.trim().length > MAX_DOT_LENGTH
    val isTireNumberTooLong: Boolean get() = tireNumber.trim().length > MAX_TIRE_NUMBER_LENGTH

    val isFormValid: Boolean
        get() = selectedAcquisitionType != null &&
                selectedProduct != null &&
                acquisitionDate.isNotBlank() &&
                cost.isNotBlank() &&
                treadDepth.isNotBlank() &&
                tireNumber.isNotBlank() &&
                dot.isNotBlank() &&
                !isDotTooLong &&
                !isTireNumberTooLong

    companion object {
        const val MAX_DOT_LENGTH = 10
        const val MAX_TIRE_NUMBER_LENGTH = 15
    }
}
