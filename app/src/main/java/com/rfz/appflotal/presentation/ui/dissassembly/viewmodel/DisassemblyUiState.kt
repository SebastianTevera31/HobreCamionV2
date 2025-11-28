package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.disassembly.DisassemblyCause
import com.rfz.appflotal.data.model.tire.Tire

sealed class OperationStatus {
    object Loading : OperationStatus()
    object Error : OperationStatus()
    object Success : OperationStatus()
}

data class DisassemblyUiState(
    val positionTire: String = "",
    val disassemblyCauseList: List<DisassemblyCause> = emptyList(),
    val destinationList: List<Destination> = emptyList(),
    val tireList: List<Tire> = emptyList(),
    val tire: Tire? = null,
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null,
)