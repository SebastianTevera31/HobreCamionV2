package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import com.rfz.appflotal.domain.CatalogItem

data class AssemblyTireUiState(
    val positionTire: String = "",
    val tireList: List<CatalogItem>? = null,
    val axleList: List<CatalogItem>? = null,
    val operationStatus: OperationStatus? = null,
)

sealed interface OperationStatus {
    data class Success(val message: String) : OperationStatus
    data class Error(val message: String) : OperationStatus
    object Loading : OperationStatus
}

