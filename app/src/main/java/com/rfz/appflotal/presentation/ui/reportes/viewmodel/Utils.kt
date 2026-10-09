package com.rfz.appflotal.presentation.ui.reportes.viewmodel

import java.text.NumberFormat
import java.util.Locale

/** Solo para mostrar: separador decimal con punto sin importar el idioma del telefono. */
fun formatDecimal(
    value: Double,
    decimals: Int = 2
): String {
    return "%.${decimals}f".format(Locale.US, value)
}

/** Solo para mostrar: dolares con formato de EUA, por ejemplo "$1,234.50". */
fun formatCurrency(
    value: Double
): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(value)
}
