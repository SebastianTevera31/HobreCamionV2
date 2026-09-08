package com.rfz.appflotal.data.repository.alerts

import com.rfz.appflotal.data.model.alerts.Alert
import com.rfz.appflotal.data.model.alerts.AlertDto
import com.rfz.appflotal.data.model.alerts.toDomain
import com.rfz.appflotal.data.network.service.alerts.RemoteAlertDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private data class AlertsQuery(
    val startDate: String,
    val endDate: String,
    val position: String,
    val alertType: String,
    val startPaging: Int
)

private data class AlertsCacheEntry(
    val result: Result<List<Alert>>,
    val timestampMs: Long
)

class AlertsRepository @Inject constructor(
    private val remoteAlertDataSource: RemoteAlertDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {
    private val cache = mutableMapOf<AlertsQuery, AlertsCacheEntry>()

    suspend fun getAlerts(
        startDate: String,
        endDate: String,
        position: String,
        alertType: String,
        startPaging: Int
    ): Result<List<Alert>> {
        val query = AlertsQuery(startDate, endDate, position, alertType, startPaging)

        cache[query]?.let { entry ->
            if (System.currentTimeMillis() - entry.timestampMs < CACHE_TTL_MS) {
                return entry.result
            }
        }

        val token = getTasksUseCase().first().first().fld_token
        val result = remoteAlertDataSource.getAlerts(
            token = token,
            startDate = startDate,
            endDate = endDate,
            position = position,
            alertType = alertType,
            startPaging = startPaging
        ).map { dtoList -> dtoList.map(AlertDto::toDomain) }

        if (result.isSuccess) {
            cache[query] = AlertsCacheEntry(result, System.currentTimeMillis())
        }
        return result
    }

    private companion object {
        const val CACHE_TTL_MS = 3 * 60 * 1000L
    }
}
