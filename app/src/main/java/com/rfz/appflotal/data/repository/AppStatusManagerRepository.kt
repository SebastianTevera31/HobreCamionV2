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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// Kotlin — versión refinada de AppStatusManagerRepository

data class AppNotificationState(
    val isMaintenance: MaintenanceStatus = MaintenanceStatus.NOT_MAINTENANCE,
    val eventType: FireCloudMessagingType = FireCloudMessagingType.NONE,
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

fun String.toSafePaymentPlanType(): PaymentPlanType =
    try {
        PaymentPlanType.valueOf(this.replace(" ", ""))
    } catch (_: Exception) {
        PaymentPlanType.None
    }

@Singleton
class AppStatusManagerRepository @Inject constructor(
    private val appUpdateRepository: AppUpdateMessageRepositoryImpl,
    private val getTasksUseCase: GetTasksUseCase,
    private val hombreCamionRepository: HombreCamionRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val processingMutex = Mutex()

    private var maintenanceJob: Job? = null

    private val _appState = MutableStateFlow(AppNotificationState())
    val appState = _appState.asStateFlow()

    init {
        observeUserData()
        observeNotifications()
    }

    private fun observeUserData() = scope.launch {
        getTasksUseCase().collect { tasks ->
            if (tasks.isNotEmpty()) {
                val userData = tasks.first()
                _appState.update { current ->
                    current.copy(
                        userId = userData.id,
                        paymentPlanType = userData.paymentPlan.toSafePaymentPlanType()
                    )
                }
            } else {
                _appState.update { current ->
                    current.copy(
                        userId = null,
                        paymentPlanType = PaymentPlanType.None
                    )
                }
            }
        }
    }

    private fun observeNotifications() = scope.launch {
        appUpdateRepository.pendingMessagesFlow.collect { notifications ->
            if (notifications.isEmpty()) {
                cleanNotificationsStateInternal()
                return@collect
            }

            val message = notifications.first()

            processingMutex.withLock {
                val currentEvent = _appState.value.eventType
                when (message.tipo) {
                    FireCloudMessagingType.MANTENIMIENTO.value,
                    FireCloudMessagingType.ARREGLO_URGENTE.value -> {
                        _appState.update { it.copy(eventType = FireCloudMessagingType.MANTENIMIENTO) }
                        handleMaintenance(message)
                    }

                    FireCloudMessagingType.CAMBIO_DE_PLAN.value -> {
                        val isAppBusy =
                            currentEvent == FireCloudMessagingType.MANTENIMIENTO ||
                                    currentEvent == FireCloudMessagingType.ACTUALIZACION

                        if (isAppBusy) {
                            appUpdateRepository.enqueueMessage(message)
                        } else {
                            _appState.update {
                                it.copy(
                                    eventType = FireCloudMessagingType.CAMBIO_DE_PLAN,
                                )
                            }
                            updateUserPlan()
                            appUpdateRepository.dequeueMessage()
                        }
                    }

                    FireCloudMessagingType.ACTUALIZACION.value -> {
                        if (currentEvent == FireCloudMessagingType.NONE) {
                            val version = message.version
                            if (isRemoteVersionGreater(version)) {
                                _appState.update { it.copy(eventType = FireCloudMessagingType.ACTUALIZACION) }
                                appUpdateRepository.dequeueMessage()
                            } else {
                                cleanNotificationsStateInternal()
                            }
                        } else {
                            appUpdateRepository.enqueueMessage(message)
                        }
                    }

                    FireCloudMessagingType.TERMINOS.value -> {
                        _appState.update { it.copy(eventType = FireCloudMessagingType.TERMINOS) }
                        appUpdateRepository.dequeueMessage()
                    }

                    else -> {
                        appUpdateRepository.dequeueMessage()
                    }
                }
            }
        }
    }

    private fun handleMaintenance(notification: AppUpdateMessage) {
        // Cancelar cualquier trabajo de mantenimiento anterior de forma segura
        maintenanceJob?.cancel()

        val datePart = notification.fecha.split(" ").firstOrNull() ?: ""
        val fechaInicioUTC = getDateFromNotification(datePart, notification.horaInicio)?.toInstant()
        val fechaFinUTC = getDateFromNotification(datePart, notification.horaFinal)?.toInstant()

        if (fechaInicioUTC == null || fechaFinUTC == null) {
            // Datos no válidos -> limpiar y salir
            scope.launch { cleanNotificationsStateInternal() }
            return
        }

        // Crear un nuevo job que monitoree el estado de mantenimiento
        maintenanceJob = scope.launch {
            try {
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
                            finalUpdateDataForUser = if (status != MaintenanceStatus.NOT_MAINTENANCE)
                                fechaFinUTC.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                            else "",
                            initialUpdateDataForUser = if (status != MaintenanceStatus.NOT_MAINTENANCE)
                                fechaInicioUTC.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                            else ""
                        )
                    }

                    if (status == MaintenanceStatus.NOT_MAINTENANCE) {
                        cleanNotificationsStateInternal()
                        break
                    }

                    delay(10_000L)
                }
            } finally {
                maintenanceJob = null
            }
        }
    }

    private fun updateUserPlan() {
        // Procesamiento en background; no bloqueante
        scope.launch {
            try {
                val userData = hombreCamionRepository.getUserData() ?: return@launch
                val currentPlan = userData.paymentPlan.toSafePaymentPlanType()

                val newPlan = when (currentPlan) {
                    PaymentPlanType.Complete -> PaymentPlanType.Free
                    PaymentPlanType.Free -> PaymentPlanType.Complete
                    else -> PaymentPlanType.Free // comportamiento por defecto
                }

                hombreCamionRepository.updateUserPlan(
                    idUser = userData.idUser,
                    plan = newPlan.name
                )
            } catch (_: Exception) {
                // Manejo de error: registrar (o exponer al logger del proyecto)
            }
        }
    }

    private fun isRemoteVersionGreater(minRemoteVersion: String): Boolean {
        val localVersion = BuildConfig.VERSION_NAME.split(".").map { it.toIntOrNull() ?: 0 }
        val remoteVersion = minRemoteVersion.split(".").map { it.toIntOrNull() ?: 0 }

        val max = maxOf(localVersion.size, remoteVersion.size)

        for (i in 0 until max) {
            val localPart = localVersion.getOrNull(i) ?: 0
            val remotePart = remoteVersion.getOrNull(i) ?: 0
            if (remotePart > localPart) return true
            if (remotePart < localPart) return false
        }
        return false
    }

    // Internal clean function para ser llamado desde coroutines internas (sin lanzar una nueva coroutine innecesaria)
    private suspend fun cleanNotificationsStateInternal() {
        maintenanceJob?.let {
            if (it.isActive) it.cancel()
            maintenanceJob = null
        }

        _appState.update { it.copy(eventType = FireCloudMessagingType.NONE) }

        try {
            appUpdateRepository.dequeueMessage()
        } catch (_: Exception) {
            // Manejo de error: registrar (o exponer al logger del proyecto)
        }
    }

    fun cleanNotificationsState() {
        scope.launch {
            // Serializar para evitar race conditions con observeNotifications
            processingMutex.withLock {
                cleanNotificationsStateInternal()
            }
        }
    }
}