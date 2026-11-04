package com.rfz.appflotal.presentation.ui.fueling.model

import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi

/**
 * Modelos de UI del módulo de cargas de combustible (solo plan Complete).
 *
 * El historial es de solo lectura: el backend no expone edición ni borrado.
 */

/** Una carga del historial, lista para mostrarse. */
data class FuelingUi(
    val date: String,          // ya convertida a la zona del dispositivo
    val fuelType: String,      // nombre del catálogo, p. ej. "Diésel(Gasóleo)"
    val units: String,         // formateado por el servidor, p. ej. "40.00 L."
    val amount: String,        // formateado por el servidor, p. ej. "$0.00"
    val station: String,       // vacío si no se capturó
    val odometer: String,      // con separador de miles
    val mapsUrl: String?       // null si la carga no trae coordenadas (0, 0)
)

/** Datos capturados en el formulario; los numéricos llegan como texto ya filtrado. */
data class FuelingFormData(
    val fuelTypeId: Int,
    val units: String,
    val isLiter: Boolean,
    val amount: String,
    val odometer: String,
    val dateMillis: Long,      // medianoche UTC del día elegido (DatePicker)
    val station: String
)

/** Estado del guardado de una carga; la navegación reacciona a [Saved]. */
enum class FuelingSaveState { Idle, Saving, Saved, Failed }

// ----------------------------------------------------------------------------
// Datos de muestra para previews.
// ----------------------------------------------------------------------------

val sampleFuelTypes = listOf(
    CatalogItemUi(1, "Diésel(Gasóleo)"),
    CatalogItemUi(2, "Gasolina"),
    CatalogItemUi(3, "Gas LP (Propano)")
)

val sampleFuelings = listOf(
    FuelingUi(
        date = "10/01/2026 10:52:47",
        fuelType = "Diésel(Gasóleo)",
        units = "40.00 L.",
        amount = "$980.00",
        station = "Pemex Av. Juárez",
        odometer = "1,200",
        mapsUrl = "https://www.google.com/maps/search/?api=1&query=19.4,-99.1"
    ),
    FuelingUi(
        date = "08/01/2026 10:52:47",
        fuelType = "Diésel(Gasóleo)",
        units = "40.00 L.",
        amount = "$975.00",
        station = "",
        odometer = "1,184",
        mapsUrl = null
    )
)
