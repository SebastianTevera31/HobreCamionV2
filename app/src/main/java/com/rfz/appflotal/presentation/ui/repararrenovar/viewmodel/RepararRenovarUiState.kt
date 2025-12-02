package com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel

import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.OperationState

data class RepararRenovarUiState(
    val repairedTireList: List<Tire> = emptyList(),
    val retreatedTireList: List<Tire> = emptyList(),
    val selectedTire: Tire? = null,
    val screenState: OperationState = OperationState.Loading,
    val operationState: OperationState? = null
)
