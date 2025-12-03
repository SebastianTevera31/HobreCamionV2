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
    @param:StringRes val title: Int,
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
        title = R.string.marca_renovado,
        route = NavScreens.MARCA_RENOVADA,
        iconRes = R.drawable.ic_brand
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
        title = R.string.monitoreo,
        route = HombreCamionScreens.MONITOR.name,
        iconRes = R.drawable.monitor
    ),
    MenuItem(
        title = R.string.pila_de_desecho,
        route = NavScreens.DESECHO,
        iconRes = R.drawable.llanta_desecho
    ),
    MenuItem(
        title = R.string.mover_a_almacen,
        route = NavScreens.REPARARRENOVAR,
        iconRes = R.drawable.ingreso_llantas
    ),
    MenuItem(
        title = R.string.cambio_de_destino,
        route = NavScreens.CAMBIO_DESTINO,
        iconRes = R.drawable.cambiar_destino
    )
)

val primaryColor = primaryLight
val primaryLight = primaryLight
val secondaryColor = secondaryLight
val cardBackground = backgroundLight
val surfaceColor = surfaceLight