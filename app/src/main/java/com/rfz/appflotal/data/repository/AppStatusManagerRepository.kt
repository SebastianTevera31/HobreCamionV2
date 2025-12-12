package com.rfz.appflotal.data.repository

import com.rfz.appflotal.BuildConfig
import com.rfz.appflotal.core.util.Commons.getDateFromNotification
import com.rfz.appflotal.data.model.fcmessaging.AppUpdateMessage
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class AppNotificationState(
    val isMaintenance: MaintenanceStatus = MaintenanceStatus.NOT_MAINTENANCE,
    val eventType: FireCloudMessagingType = FireCloudMessagingType.NONE,
    val isUpdate: Boolean = false,
    val wasAPlanChange: Boolean = false,
    val paymentPlanType: PaymentPlanType = PaymentPlanType.None,
    val userId: Int? = 0,
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
    private val appUpdateRepository: AppUpdateMessageRepositoryImpl,
    private val getTasksUseCase: GetTasksUseCase,
    private val hombreCamionRepository: HombreCamionRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var maintenanceJob: Job? = null

    private val _appState = MutableStateFlow(AppNotificationState())
    val appState = _appState.asStateFlow()

    init {
        observeUserData()
        observeNotifications()
    }

    private fun observeUserData() = scope.launch {
        getTasksUseCase().collect { observeUserData ->
            if (observeUserData.isNotEmpty()) {
                _appState.update { currentState ->
                    val userData = observeUserData.first()
                    currentState.copy(
                        userId = userData.id,
                        paymentPlanType = PaymentPlanType.valueOf(
                            userData.paymentPlan.replace(" ", "")
                        )
                    )
                }
            } else {
                _appState.update { currentState ->
                    currentState.copy(
                        userId = null,
                        paymentPlanType = PaymentPlanType.None
                    )
                }
            }
        }
    }

    private fun observeNotifications() = scope.launch {
        appUpdateRepository.updateFlow.collect { notification ->
            if (notification == null) return@collect

            when (notification.tipo) {
                FireCloudMessagingType.MANTENIMIENTO.value, FireCloudMessagingType.ARREGLO_URGENTE.value -> {
                    _appState.update { currentUiState ->
                        currentUiState.copy(
                            eventType = FireCloudMessagingType.MANTENIMIENTO
                        )
                    }
                    handleMaintenance(notification)
                }

                FireCloudMessagingType.CAMBIO_DE_PLAN.value -> {
                    if (_appState.value.isMaintenance == MaintenanceStatus.MAINTENANCE || _appState.value.eventType ==
                        FireCloudMessagingType.ACTUALIZACION
                    ) {
                        _appState.update { currentState ->
                            currentState.copy(wasAPlanChange = true)
                        }
                    } else {
                        _appState.update { currentUiState ->
                            currentUiState.copy(
                                eventType = FireCloudMessagingType.CAMBIO_DE_PLAN,
                                wasAPlanChange = false
                            )
                        }
                        updateUserPlan()
                    }
                }

                FireCloudMessagingType.ACTUALIZACION.value -> {
                    isRemoteVersionDifferent(notification.version)
                }

                FireCloudMessagingType.TERMINOS.value -> {
                    _appState.update { currentUiState ->
                        currentUiState.copy(
                            eventType = FireCloudMessagingType.TERMINOS
                        )
                    }
                }
            }
        }
    }

    private fun handleMaintenance(notification: AppUpdateMessage) {
        // Cancelar cualquier trabajo de mantenimiento anterior
        maintenanceJob?.cancel()

        val fechaInicioUTC = getDateFromNotification(
            notification.fecha.split(" ")[0],
            notification.horaInicio
        )?.toInstant()

        val fechaFinUTC = getDateFromNotification(
            notification.fecha.split(" ")[0],
            notification.horaFinal
        )?.toInstant()

        if (fechaInicioUTC == null || fechaFinUTC == null) return

        // Lanzar el nuevo trabajo de monitoreo
        maintenanceJob = scope.launch {
            CoroutineScope(Dispatchers.IO + SupervisorJob())
            while (isActive) {
                val ahoraUTC = Instant.now()

                val status = when {
                    ahoraUTC.isBefore(fechaInicioUTC) -> MaintenanceStatus.SCHEDULED
                    ahoraUTC.isAfter(fechaInicioUTC) && ahoraUTC.isBefore(fechaFinUTC) -> MaintenanceStatus.MAINTENANCE
                    else -> MaintenanceStatus.NOT_MAINTENANCE
                }

                _appState.update {
                    it.copy(
                        isMaintenance = status,
                        finalUpdateDataForUser = fechaFinUTC.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        initialUpdateDataForUser = fechaInicioUTC.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    )
                }

                if (status == MaintenanceStatus.NOT_MAINTENANCE) {
                    // Al terminar, solo reseteamos el estado de mantenimiento, no el de usuario
                    _appState.update {
                        it.copy(
                            isMaintenance = MaintenanceStatus.NOT_MAINTENANCE,
                            finalUpdateDataForUser = "",
                            initialUpdateDataForUser = ""
                        )
                    }
                    break // Rompe el loop de este trabajo
                }

                delay(10_000)
            }
        }
    }

    fun updateUserPlan() {
        scope.launch {
            val paymentPlan = _appState.value.paymentPlanType
            val userId = _appState.value.userId
            if (userId != null) {
                if (paymentPlan != PaymentPlanType.Complete && paymentPlan != PaymentPlanType.None) {
                    hombreCamionRepository.updateUserPlan(
                        idUser = userId,
                        plan = PaymentPlanType.Complete.name
                    )

                } else if (paymentPlan == PaymentPlanType.Complete) {
                    hombreCamionRepository.updateUserPlan(
                        idUser = userId,
                        plan = PaymentPlanType.Free.name
                    )
                }
            }
        }
    }

    private fun isRemoteVersionDifferent(minRemoteVersion: String) {
        val localVersion = BuildConfig.VERSION_NAME.split(".").map { it.toIntOrNull() ?: 0 }
        val remoteVersion = minRemoteVersion.split(".").map { it.toIntOrNull() ?: 0 }

        val max = maxOf(localVersion.size, remoteVersion.size)

        for (i in 0 until max) {
            val localPart = localVersion.getOrNull(i) ?: 0
            val remotePart = remoteVersion.getOrNull(i) ?: 0
            if (remotePart > localPart) {
                _appState.update { it.copy(eventType = FireCloudMessagingType.ACTUALIZACION) }
                return
            }
            if (remotePart < localPart) {
                cleanNotificationsState()
                return
            }
        }
    }

    fun cleanNotificationsState() {
        scope.launch {
            appUpdateRepository.clear()
            maintenanceJob?.let {
                if (it.isActive) it?.cancel()
            }
            _appState.update { it.copy(eventType = FireCloudMessagingType.NONE) }

            // Verificacion de Evento Pendiente
            if (_appState.value.wasAPlanChange) {
                appUpdateRepository.saveMessage(
                    AppUpdateMessage(
                        tipo = FireCloudMessagingType.CAMBIO_DE_PLAN.value,
                        fecha = "",
                        horaInicio = "",
                        horaFinal = "",
                        version = ""
                    )
                )
            }
        }
    }
}