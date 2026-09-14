package com.rfz.appflotal.presentation.ui.services.model

/**
 * Modelos de UI del módulo de servicios.
 *
 * Son puramente de presentación para que las pantallas se puedan previsualizar y
 * maquetar sin depender del backend. Cuando existan los endpoints de lectura
 * (GetServiceOrders / GetServiceOrderDetails) se mapearán los DTO a estos modelos
 * en el ViewModel, igual que se hace en el módulo de alertas.
 */

enum class ServiceOrderStatus { ABIERTA, FINALIZADA }

/** Cabecera con la información del vehículo dueño de las órdenes. */
data class VehicleHeaderUi(
    val economicNumber: String,   // p. ej. "SC"
    val description: String,      // p. ej. "IRIZAR i8 6x2"
    val odometer: String          // ya formateado, p. ej. "12,345,523 km"
)

/** Una orden de servicio en la lista. Mapea a ServiceOrderDto + estado/cierre. */
data class ServiceOrderUi(
    val id: Int,
    val folio: String,                 // idServiceCustom, legible para el usuario
    val summary: String,               // resumen: servicio principal o "N servicios"
    val openingDate: String,           // fld_registrationDate formateada
    val closingDate: String?,          // fecha de cierre (pendiente en el DTO); null = abierta
    val status: ServiceOrderStatus,
    val total: String,                 // total ya formateado, p. ej. "$1,800"
    val itemCount: Int
)

/** Un servicio/detalle dentro de una orden. Mapea a ServiceDetailDto. */
data class ServiceItemUi(
    val id: Int,
    val serviceId: Int,
    val serviceName: String,           // nombre del catálogo (ServiceResponseDto)
    val quantity: Double,
    val unitCost: Double,
    val provider: String,              // fld_provider
    val occurrenceType: String,        // c_typeOfOccurrence_fk_1 (nombre)
    val notes: String                  // fld_notes
) {
    val total: Double get() = quantity * unitCost
}

/** Ítem de catálogo para los dropdowns (servicio, proveedor, tipo de ocurrencia). */
data class CatalogItemUi(val id: Int, val name: String)

// ----------------------------------------------------------------------------
// Datos de muestra para previews (se eliminarán al conectar el backend).
// ----------------------------------------------------------------------------

val sampleVehicleHeader = VehicleHeaderUi(
    economicNumber = "SC",
    description = "IRIZAR i8 6x2",
    odometer = "12,345,523 km"
)

val sampleServiceOrders = listOf(
    ServiceOrderUi(
        id = 1,
        folio = "12344",
        summary = "Balanceo · Alineación",
        openingDate = "12/04/24",
        closingDate = null,
        status = ServiceOrderStatus.ABIERTA,
        total = "$1,800",
        itemCount = 2
    ),
    ServiceOrderUi(
        id = 2,
        folio = "33343",
        summary = "Vuelta de tuercas",
        openingDate = "12/04/24",
        closingDate = "15/04/24",
        status = ServiceOrderStatus.FINALIZADA,
        total = "$600",
        itemCount = 1
    ),
    ServiceOrderUi(
        id = 3,
        folio = "33355",
        summary = "Renovado · Reparación",
        openingDate = "23/05/24",
        closingDate = "23/05/26",
        status = ServiceOrderStatus.FINALIZADA,
        total = "$3,400",
        itemCount = 3
    )
)

val sampleServiceItems = listOf(
    ServiceItemUi(
        id = 1,
        serviceId = 10,
        serviceName = "Balanceo",
        quantity = 1.0,
        unitCost = 600.0,
        provider = "Llantera del Norte",
        occurrenceType = "Preventivo",
        notes = "Balanceo de eje delantero."
    ),
    ServiceItemUi(
        id = 2,
        serviceId = 11,
        serviceName = "Renovado",
        quantity = 1.0,
        unitCost = 1200.0,
        provider = "Renovadora RFZ",
        occurrenceType = "Correctivo",
        notes = "Renovado de dos llantas traseras."
    )
)

val sampleServiceCatalog = listOf(
    CatalogItemUi(10, "Balanceo"),
    CatalogItemUi(11, "Renovado"),
    CatalogItemUi(12, "Alineación"),
    CatalogItemUi(13, "Reparación"),
    CatalogItemUi(14, "Vuelta de tuercas")
)

val sampleOccurrenceTypes = listOf(
    CatalogItemUi(1, "Preventivo"),
    CatalogItemUi(2, "Correctivo"),
    CatalogItemUi(3, "Garantía")
)

val sampleProviders = listOf(
    CatalogItemUi(1, "Llantera del Norte"),
    CatalogItemUi(2, "Renovadora RFZ"),
    CatalogItemUi(3, "Taller Central")
)
