package com.rfz.appflotal.data.repository

import com.rfz.appflotal.core.util.Commons.getDateFromNotification
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class MainUiState(
    val isMaintenance: MaintenanceStatus = MaintenanceStatus.NOT_MAINTENANCE,
    val finalUpdateDataForUser: String = "",
    val initialUpdateDataForUser: String = ""
)

enum class MaintenanceStatus {
    MAINTENANCE,
    NOT_MAINTENANCE,
    SCHEDULED
}


@Singleton
class AppStatusManagerRepository @Inject constructor(
    private val appUpdateRepository: AppUpdateMessageRepositoryImpl
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _mainUiState = MutableStateFlow(MainUiState())


    val updateMessage =
        appUpdateRepository.updateFlow
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    val mainUiState = _mainUiState.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() = scope.launch {
        appUpdateRepository.updateFlow.collect { notification ->

            if (notification?.tipo != FireCloudMessagingType.MANTENIMIENTO.value) {
                _mainUiState.update {
                    it.copy(isMaintenance = MaintenanceStatus.NOT_MAINTENANCE)
                }
                return@collect
            }

            // Extraer fechas
            val fechaInicioUTC = getDateFromNotification(
                notification.fecha.split(" ")[0],
                notification.horaInicio
            )?.toInstant()

            val fechaFinUTC = getDateFromNotification(
                notification.fecha.split(" ")[0],
                notification.horaFinal
            )?.toInstant()

            if (fechaInicioUTC == null || fechaFinUTC == null) return@collect

            // Lanzar un único loop que revisa el horario cada X tiempo
            launch {
                while (isActive) {
                    try {
                        val ahoraUTC = Instant.now()

                        val status =
                            when {
                                ahoraUTC.isBefore(fechaInicioUTC) -> MaintenanceStatus.SCHEDULED
                                ahoraUTC.isAfter(fechaInicioUTC) && ahoraUTC.isBefore(fechaFinUTC) ->
                                    MaintenanceStatus.MAINTENANCE

                                else -> MaintenanceStatus.NOT_MAINTENANCE
                            }

                        val finalUserDate = fechaFinUTC
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        val initialUserDate = fechaInicioUTC
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))


                        _mainUiState.update {
                            it.copy(
                                isMaintenance = status,
                                finalUpdateDataForUser = finalUserDate,
                                initialUpdateDataForUser = initialUserDate
                            )
                        }

                        if (status == MaintenanceStatus.NOT_MAINTENANCE) {
                            _mainUiState.value = MainUiState()
                            // rompes SOLO el loop, NO el scope completo
                            break
                        }

                        delay(10_000)
                    } catch (_: Exception) { }
                }
            }
        }
    }
}