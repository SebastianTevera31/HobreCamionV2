package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

import com.rfz.appflotal.data.model.catalog.TireInspectionItem
import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.disassembly.DisassemblyCause
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.presentation.ui.dissassembly.screen.NavigationScreen
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUi
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

data class DisassemblyUiState(
    val positionTire: String = "",
    val initialPressure: Int? = null,
    val initialTemperature: Int? = null,
    val disassemblyCauseList: List<DisassemblyCause> = emptyList(),
    val destinationList: List<Destination> = emptyList(),
    val tireList: List<Tire> = emptyList(),
    val tire: Tire? = null,
    val screenLoadStatus: OperationStatus = OperationStatus.Loading,
    val operationStatus: OperationStatus? = null,
    val lastOdometer: Int = 0,
    val tireReportList: List<TireInspectionItem> = emptyList(),
    val navigationScreen: NavigationScreen = NavigationScreen.INSPECTION,
    val inspectionForm: InspectionUi = InspectionUi(),
    val temperatureUnit: UnidadTemperatura = UnidadTemperatura.CELCIUS,
    val pressureUnit: UnidadPresion = UnidadPresion.PSI,
    val odometerUnit: UnidadOdometro = UnidadOdometro.KILOMETROS
)