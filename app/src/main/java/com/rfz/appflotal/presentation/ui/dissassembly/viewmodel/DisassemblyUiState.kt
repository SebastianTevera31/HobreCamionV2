package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

sealed class OperationStatus {
    object Loading : OperationStatus()
    object Error : OperationStatus()
    object Success : OperationStatus()
}

data class DisassemblyUiState(
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus = OperationStatus.Loading,
)