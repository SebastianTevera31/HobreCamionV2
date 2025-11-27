package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.annotation.StringRes
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.Tire

enum class OdometerValidation(@param:StringRes val message: Int? = null) {
    VALID,
    INVALID(R.string.error_odometro_inferior),
    EMPTY(R.string.requerido)
}

sealed class ScreenLoadStatus {
    object Loading : ScreenLoadStatus()
    object Error : ScreenLoadStatus()
    object Success : ScreenLoadStatus()
}

data class AssemblyTireUiState(
    val positionTire: String = "",
    val currentOdometer: String = "0",
    val currentTire: Tire? = null,
    val isOdometerValid: OdometerValidation = OdometerValidation.EMPTY,
    val tireList: List<Tire> = emptyList(),
    val axleList: List<CatalogItem> = emptyList(),
    val screenLoadStatus: ScreenLoadStatus = ScreenLoadStatus.Loading,
    val operationStatus: OperationStatus? = null,
)

sealed interface OperationStatus {
    data class Success(val message: String) : OperationStatus
    data class Error(val message: String) : OperationStatus
    object Loading : OperationStatus
}

