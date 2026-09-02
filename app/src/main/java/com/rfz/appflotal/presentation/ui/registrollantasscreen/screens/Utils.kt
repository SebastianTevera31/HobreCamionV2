package com.rfz.appflotal.presentation.ui.registrollantasscreen.screens

import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.home.utils.MenuItem

val tireMenuItems = listOf(
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
    )
)