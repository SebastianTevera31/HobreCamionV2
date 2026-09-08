package com.rfz.appflotal.domain.alerts

import com.rfz.appflotal.data.model.alerts.Alert
import com.rfz.appflotal.data.repository.alerts.AlertsRepository
import javax.inject.Inject

class GetAlertsUseCase @Inject constructor(private val alertsRepository: AlertsRepository) {
    suspend operator fun invoke(
        startDate: String,
        endDate: String,
        position: String,
        alertType: String,
        startPaging: Int
    ): Result<List<Alert>> {
        return alertsRepository.getAlerts(
            startDate = startDate,
            endDate = endDate,
            position = position,
            alertType = alertType,
            startPaging = startPaging
        )
    }
}