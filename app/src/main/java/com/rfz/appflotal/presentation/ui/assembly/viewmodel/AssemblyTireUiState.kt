package com.rfz.appflotal.presentation.ui.assembly.viewmodel

import com.rfz.appflotal.domain.CatalogItem

data class AssemblyTireUiState(
    val listOfTires: CatalogItem? = null,
    val listOfAxles: CatalogItem? = null,
    val axleSelected: CatalogItem? = null,
    val tireSelected: CatalogItem? = null,
    val positionTire: String = "",
    val odometer: String = "",
    val assemblyDate: String = "",
)