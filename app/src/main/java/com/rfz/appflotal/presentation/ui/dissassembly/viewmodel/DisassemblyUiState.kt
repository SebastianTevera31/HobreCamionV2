package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

sealed class ScreenLoadStatus {
    object Loading : ScreenLoadStatus()
    object Error : ScreenLoadStatus()
    object Success : ScreenLoadStatus()
}

data class DisassemblyUiState(
    val screenLoadStatus: ScreenLoadStatus = ScreenLoadStatus.Loading,
    val disassemblyTire: List<Disassembly> = emptyList(),
)

data class Disassembly(
    val id: String,
    val name: String,
)