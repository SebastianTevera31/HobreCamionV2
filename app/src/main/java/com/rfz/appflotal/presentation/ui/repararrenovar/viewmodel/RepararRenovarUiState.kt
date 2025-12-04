package com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.repair.RepairCause
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.domain.retreaddesign.RetreadDesign
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

data class RepararRenovarUiState(
    val repairedTireList: List<Tire> = emptyList(),
    val retreadedTireList: List<Tire> = emptyList(),
    val originList: List<Destination> = emptyList(),
    val retreadDesignList: List<RetreadDesign> = emptyList(),
    val repairCauseList: List<RepairCause> = emptyList(),
    val tireCost: String = "",
    val selectedTire: Tire? = null,
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null,
    val selectedRetreadedDesign: RetreadDesign? = null,
    val selectedOrigin: CatalogItem? = null,
    val selectedRepairCause: CatalogItem? = null
)

enum class DestinationSelection(val id: Int) {
    REPARAR(1),
    RENOVAR(6)
}