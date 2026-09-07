package com.rfz.appflotal.data.network.service.alerts

import com.rfz.appflotal.data.network.client.alerts.AlertsService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteAlertDataSource @Inject constructor(private val alertService: AlertsService) {
    suspend fun getAlerts(
        token: String,
        startDate: String,
        endDate: String,
        position: String,
        alertType: String,
        startPaging: String
    ) = networkRequestHelper {
        alertService.getAlerts(
            token = "Bearer $token",
            startDate = startDate,
            endDate = endDate,
            position = position,
            alertType = alertType,
            startPaging = startPaging
        )
    }
}