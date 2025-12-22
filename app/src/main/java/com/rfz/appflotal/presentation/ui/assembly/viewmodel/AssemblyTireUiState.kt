package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import androidx.annotation.StringRes
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.axle.Axle
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

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
    val tireList: List<Tire> = emptyList(),
    val axleList: List<Axle> = emptyList(),
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null,
    val odometerUnit: UnidadOdometro = UnidadOdometro.KILOMETROS
)
