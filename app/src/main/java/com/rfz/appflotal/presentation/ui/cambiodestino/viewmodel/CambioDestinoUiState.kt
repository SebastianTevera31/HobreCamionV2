package com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

data class CambioDestinoUiState(
    val tires: List<Tire> = emptyList(),
    val originList: List<Destination> = emptyList(),
    val destinationList: List<Destination> = emptyList(),
    val selectedTireList: List<Tire> = emptyList(),
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null,
    val form: CambioDestinoFormState = CambioDestinoFormState(),
)

data class CambioDestinoFormState(
    val selectedTire: Tire? = null,
    val selectedOrigin: CatalogItem? = null,
    val selectedDestination: CatalogItem? = null,
    val reason: String = "",
)
