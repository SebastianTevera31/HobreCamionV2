package com.rfz.appflotal.presentation.ui.scrap.viewmodel

import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.data.model.waster.WasteReport
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

data class TireWasteUiState(
    val wasteReportList: List<WasteReport> = emptyList(),
    val dismountedTireList: List<Tire> = emptyList(),
    val selectedTire: Tire? = null,
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null
)