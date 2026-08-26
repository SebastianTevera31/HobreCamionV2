package com.rfz.appflotal.presentation.ui.tiremanagment.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.ui.graphics.vector.ImageVector
import com.rfz.appflotal.data.model.tiremanagement.TireManagementItem

data class TireManagementUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentScreen: TireManagementDestinations = TireManagementDestinations.Tire,
    val items: List<TireManagementItem> = emptyList()
)

enum class TireManagementDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    Tire("tire", "Tire", Icons.Default.CarRepair, "Tire"),
    Catalogs(
        "catalogs",
        "Catalogs",
        Icons.AutoMirrored.Filled.ListAlt,
        "Catalogs"
    )
}