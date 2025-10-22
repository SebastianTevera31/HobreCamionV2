package com.rfz.appflotal.presentation.ui.home.utils

import androidx.annotation.StringRes
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.HombreCamionScreens
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.presentation.theme.backgroundLight
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.theme.surfaceLight

data class MenuItem(
    @StringRes val title: Int,
    val route: String,
    val iconRes: Int
)

val menuItems = listOf(
    MenuItem(
        R.string.brands,
        NavScreens.MARCAS,
        R.drawable.ic_brand
    ),
    MenuItem(
        R.string.original_design,
        NavScreens.ORIGINAL,
        R.drawable.ic_tire_design
    ),
    MenuItem(
        R.string.dise_os_renovados,
        NavScreens.RENOVADOS,
        R.drawable.ic_tire_design
    ),
    MenuItem(
        R.string.tire_sizes,
        NavScreens.MEDIDAS_LLANTAS,
        R.drawable.ic_tire_size
    ),
    MenuItem(
        R.string.products,
        NavScreens.PRODUCTOS,
        R.drawable.ic_products
    ),
    MenuItem(
        R.string.tire_register,
        NavScreens.REGISTRO_LLANTAS,
        R.drawable.ic_tire_register
    ),
    MenuItem(
        R.string.vehicle_register,
        NavScreens.REGISTRO_VEHICULOS,
        R.drawable.ic_truck
    ),
    MenuItem(
        R.string.tire_change,
        NavScreens.MONTAJE_DESMONTAJE,
        R.drawable.ic_tire_change
    ),
    MenuItem(
        title = R.string.monitoreo,
        route = HombreCamionScreens.MONITOR.name,
        iconRes = R.drawable.monitor
    ),
    MenuItem(
        title = R.string.marca_renovada,
        route = NavScreens.MARCA_RENOVADA,
        iconRes = R.drawable.ic_brand
    )
)

val primaryColor = primaryLight
val primaryLight = primaryLight
val secondaryColor = secondaryLight
val cardBackground = backgroundLight
val surfaceColor = surfaceLight