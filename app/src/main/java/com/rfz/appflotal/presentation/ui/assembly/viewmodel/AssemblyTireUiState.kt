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


data class AssemblyTireUiState(
    val positionTire: String = "",
    val currentOdometer: String = "0",
    val currentTire: Tire? = null,
    val isOdometerValid: OdometerValidation = OdometerValidation.EMPTY,
    val tireList: List<Tire>? = null,
    val axleList: List<CatalogItem>? = null,
    val operationStatus: OperationStatus? = null,
)

sealed interface OperationStatus {
    data class Success(val message: String) : OperationStatus
    data class Error(val message: String) : OperationStatus
    object Loading : OperationStatus
}

