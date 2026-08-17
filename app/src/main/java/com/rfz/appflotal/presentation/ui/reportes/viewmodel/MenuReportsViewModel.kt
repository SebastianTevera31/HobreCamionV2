package com.rfz.appflotal.presentation.ui.reportes.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.lifecycle.ViewModel
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.VehicleStat
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MenuReportsUi(
    val vehicleName: String = "Mercedes Actros",
    val vehiclePlate: String = "4521-KBX",
    val stats: List<VehicleStat> = listOf(
        VehicleStat(Icons.Outlined.LocalShipping.asIcon(), "50", "km/lts", "Rendimiento"),
        VehicleStat(Icons.Outlined.GpsFixed.asIcon(), "1000", "km/mm", "Desgaste"),
        VehicleStat(Icons.Outlined.Cloud.asIcon(), "100", "kg", "Emisión CO2")
    )
)

@HiltViewModel
class MenuReportsViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(MenuReportsUi())
    val uiState = _uiState.asStateFlow()
}