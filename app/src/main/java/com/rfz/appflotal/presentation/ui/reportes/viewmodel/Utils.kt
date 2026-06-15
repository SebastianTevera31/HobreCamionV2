package com.rfz.appflotal.presentation.ui.reportes.viewmodel

fun formatDecimal(
    value: Double,
    decimals: Int = 2
): String {
    return "%.${decimals}f".format(value)
}

fun formatCurrency(
    value: Double
): String {
    return "$${formatDecimal(value)}"
}