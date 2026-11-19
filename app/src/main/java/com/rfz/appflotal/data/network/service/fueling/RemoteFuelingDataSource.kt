package com.rfz.appflotal.data.network.service.fueling

import android.net.Uri
import android.util.Log
import com.rfz.appflotal.data.model.fueling.FUELING_TICKET_PART
import com.rfz.appflotal.data.model.fueling.FuelingHistoryResponse
import com.rfz.appflotal.data.model.fueling.InsertFuelingDto
import com.rfz.appflotal.data.network.client.fueling.FuelingService
import com.rfz.appflotal.data.network.multipart.ImagePartFactory
import com.rfz.appflotal.data.network.networkRequestHelper
import com.rfz.appflotal.data.network.service.DataError
import javax.inject.Inject

private const val TAG = "RemoteFuelingDataSource"

class RemoteFuelingDataSource @Inject constructor(
    private val fuelingService: FuelingService,
    private val imagePartFactory: ImagePartFactory
) {
    /** La foto del ticket es obligatoria: si no se puede leer, no se envía la carga. */
    suspend fun insertFueling(
        token: String,
        request: InsertFuelingDto,
        ticket: Uri
    ): Result<Unit> {
        val ticketPart = imagePartFactory.fromUri(FUELING_TICKET_PART, ticket, filePrefix = "ticket")
            ?: return Result.failure(IllegalStateException("No se pudo leer la foto del ticket"))
        return networkRequestHelper {
            fuelingService.insertFueling(
                token = "Bearer $token",
                fuelType = request.fuelType,
                fuelUnits = request.fuelUnits,
                isLiter = request.isLiter,
                amount = request.amount,
                odometer = request.odometer,
                fuelingDate = request.fuelingDate,
                station = request.station.takeIf { it.isNotBlank() },
                lat = request.lat,
                lon = request.lon,
                percentage = request.percentage,
                fld_imageTicket = ticketPart
            )
        }.onFailure { error ->
            // Deja en Logcat el código y el mensaje que devolvió el backend (p. ej. un 500).
            val detail = (error as? DataError.Http)?.let { "HTTP ${it.code}: ${it.body}" } ?: error.toString()
            Log.e(TAG, "insertFueling falló -> $detail")
        }
    }

    suspend fun getFuelingHistory(
        token: String,
        startDate: String?,
        endDate: String?,
        fuelType: Int?,
        page: Int,
        pageSize: Int
    ): Result<FuelingHistoryResponse> = networkRequestHelper {
        fuelingService.getFuelingHistory(
            token = "Bearer $token",
            startDate = startDate,
            endDate = endDate,
            fuelType = fuelType,
            start = page,
            pageSize = pageSize
        )
    }
}
