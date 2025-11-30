package com.rfz.appflotal.presentation.ui.inspection.viewmodel

import com.rfz.appflotal.data.model.CatalogItem

sealed interface InspectionUiState {
    data object Loading : InspectionUiState
    data class Error(val message: String) : InspectionUiState
    data object Empty : InspectionUiState
    data class Success(
        val inspectionList: List<CatalogItem>,
        val lastOdometer: Int,
        val isOdometerEditable: Boolean
    ) : InspectionUiState
}

sealed class OperationState {
    object Loading : OperationState()
    object Error : OperationState()
    object Success : OperationState()
}

data class InspectionRequestState(
    val isSending: Boolean = false,
    val message: String? = null,
    val operationState: OperationState = OperationState.Loading
)

data class InspectionUi(
    val reportId: String?,
    val odometer: Int,
    val temperature: Int,
    val pressure: Int,
    val adjustedPressure: Int,
    val treadDepth1: Float,
    val treadDepth2: Float,
    val treadDepth3: Float,
    val treadDepth4: Float,
)

fun String.filterNumericDot(): String {
    // Solo dígitos y un único punto; perfecto para permitir decimales si los necesitas.
    val filtered = buildString {
        var dotSeen = false
        for (ch in this@filterNumericDot) {
            if (ch.isDigit()) append(ch)
            else if (ch == '.' && !dotSeen) {
                append(ch)
                dotSeen = true
            }
        }
    }
    return filtered
}

fun treadDeptError(td1: Int?, td4: Int?): Boolean? {
    if (td1 == null || td4 == null) return true
    return if (td1 > 0 && td4 > 0) null else true
}