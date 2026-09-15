package com.rfz.appflotal.presentation.ui.services.model

/**
 * Modelos de UI del módulo de servicios (flujo "solo servicios", sin órdenes).
 *
 * Un servicio es plano: tipo, descripción, proveedor, precio, cantidad y fecha,
 * asociado a un vehículo. El total se deriva de precio × cantidad.
 */

/** Cabecera con la información del vehículo. */
data class VehicleHeaderUi(
    val economicNumber: String,   // p. ej. "SC"
    val description: String,      // p. ej. "IRIZAR i8 6x2"
    val odometer: String          // ya formateado, p. ej. "12,345,523 km"
)

/**
 * Un servicio. Mapea a ServiceResponseDto (lectura) / ServiceDetailDto (CRUD).
 *
 * Nota: el endpoint de lectura no devuelve `provider` ni `date`, así que en la
 * lista y al editar esos campos llegan vacíos (limitación del backend actual).
 */
data class ServiceUi(
    val id: Int,
    val type: String,            // fld_typeService (nombre)
    val description: String,     // fld_description
    val price: Int,              // fld_price (unitario)
    val quantity: Int,           // fld_cant
    val provider: String = "",   // fld_provider (no viene en lectura)
    val date: String = ""        // fld_date (no viene en lectura)
) {
    val total: Int get() = price * quantity
}

/** Ítem de catálogo para el dropdown de tipos de servicio. */
data class CatalogItemUi(val id: Int, val name: String)

// ----------------------------------------------------------------------------
// Datos de muestra para previews.
// ----------------------------------------------------------------------------

val sampleVehicleHeader = VehicleHeaderUi(
    economicNumber = "SC",
    description = "IRIZAR i8 6x2",
    odometer = "12,345,523 km"
)

val sampleServices = listOf(
    ServiceUi(
        id = 1,
        type = "Preventivo",
        description = "Balanceo de eje delantero",
        price = 600,
        quantity = 1
    ),
    ServiceUi(
        id = 2,
        type = "Correctivo",
        description = "Renovado de dos llantas traseras",
        price = 1200,
        quantity = 2
    ),
    ServiceUi(
        id = 3,
        type = "Preventivo",
        description = "Alineación",
        price = 450,
        quantity = 1
    )
)

val sampleServiceTypes = listOf(
    CatalogItemUi(1, "Preventivo"),
    CatalogItemUi(2, "Correctivo"),
    CatalogItemUi(3, "Garantía")
)
