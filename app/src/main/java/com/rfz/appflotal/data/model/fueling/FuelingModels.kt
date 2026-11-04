package com.rfz.appflotal.data.model.fueling

import com.google.gson.annotations.SerializedName

/** Body de `api/Vehicle/insertFueling`. El vehículo lo resuelve el servidor por el token. */
data class InsertFuelingDto(
    @SerializedName("fuelType") val fuelType: Int,
    @SerializedName("fuelUnits") val fuelUnits: Double,
    @SerializedName("isLiter") val isLiter: Boolean,
    @SerializedName("amount") val amount: Double,
    @SerializedName("odometer") val odometer: Int,
    // ISO 8601 UTC, p. ej. 2026-11-04T16:28:20.566Z
    @SerializedName("fuelingDate") val fuelingDate: String,
    @SerializedName("station") val station: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

/** Respuesta paginada de `api/Vehicle/getFuelingHistory`. */
data class FuelingHistoryResponse(
    @SerializedName("data") val data: List<FuelingRecordDto>? = null,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("pageSize") val pageSize: Int = 0
)

/** Un registro del historial. [units] y [amount] llegan ya formateados por el servidor. */
data class FuelingRecordDto(
    @SerializedName("fld_fuelingDate") val fuelingDate: String? = null,
    @SerializedName("vehicle") val vehicle: String? = null,
    @SerializedName("units") val units: String? = null,
    @SerializedName("fuelType") val fuelType: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("fld_station") val station: String? = null,
    @SerializedName("fld_odometer") val odometer: Int = 0,
    @SerializedName("fld_lat") val lat: Double = 0.0,
    @SerializedName("fld_lon") val lon: Double = 0.0
)

/** Carga de combustible tal como la usa la app (sin formato de pantalla). */
data class FuelingRecord(
    val fuelingDate: String,
    val vehicle: String,
    val units: String,
    val fuelType: String,
    val amount: String,
    val station: String,
    val odometer: Int,
    val lat: Double,
    val lon: Double
) {
    val hasCoordinates: Boolean get() = lat != 0.0 || lon != 0.0
}

data class FuelingPage(
    val items: List<FuelingRecord>,
    val page: Int,
    val total: Int,
    val hasNextPage: Boolean
)

/** Datos que captura el usuario para registrar una carga. */
data class NewFueling(
    val fuelTypeId: Int,
    val fuelUnits: Double,
    val isLiter: Boolean,
    val amount: Double,
    val odometer: Int,
    val fuelingDate: String,
    val station: String
)

fun FuelingRecordDto.toDomain() = FuelingRecord(
    fuelingDate = fuelingDate.orEmpty(),
    vehicle = vehicle.orEmpty(),
    units = units.orEmpty(),
    fuelType = fuelType.orEmpty(),
    amount = amount.orEmpty(),
    // El servidor devuelve "N/A" cuando no se capturó estación.
    station = station?.takeUnless { it.isBlank() || it.equals("N/A", ignoreCase = true) }.orEmpty(),
    odometer = odometer,
    lat = lat,
    lon = lon
)

/**
 * [requestedPage] es la página pedida; se usa si el servidor no la devuelve.
 * Hay página siguiente mientras lo mostrado hasta esta página no alcance [FuelingHistoryResponse.total].
 */
fun FuelingHistoryResponse.toDomain(requestedPage: Int, requestedPageSize: Int): FuelingPage {
    val items = data.orEmpty().map { it.toDomain() }
    val currentPage = page.takeIf { it > 0 } ?: requestedPage
    val size = pageSize.takeIf { it > 0 } ?: requestedPageSize
    return FuelingPage(
        items = items,
        page = currentPage,
        total = total,
        hasNextPage = items.isNotEmpty() && currentPage * size < total
    )
}

fun NewFueling.toDto(lat: Double, lon: Double) = InsertFuelingDto(
    fuelType = fuelTypeId,
    fuelUnits = fuelUnits,
    isLiter = isLiter,
    amount = amount,
    odometer = odometer,
    fuelingDate = fuelingDate,
    station = station.trim(),
    lat = lat,
    lon = lon
)
