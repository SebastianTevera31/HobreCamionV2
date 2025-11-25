package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import com.rfz.appflotal.data.model.axle.Axle
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.domain.CatalogItem

data class AssemblyTireUiState(
    val tireList: List<Tire>? = null,
    val axleList: List<Axle>? = null,
    val axleSelected: CatalogItem? = null,
    val tireSelected: CatalogItem? = null,
    val positionTire: String = "",
    val odometer: Int = 0,
    val assemblyDate: String = "",
    val operationStatus: OperationStatus? = null
)

sealed interface OperationStatus {
    data class AssemblyTire(val message: String) : OperationStatus
    data class Error(val message: String) : OperationStatus
    object Loading : OperationStatus
}

