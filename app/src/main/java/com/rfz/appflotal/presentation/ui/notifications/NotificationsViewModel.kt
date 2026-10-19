package com.rfz.appflotal.presentation.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.catalog.MaintenanceNotification
import com.rfz.appflotal.data.repository.maintenance.MaintenanceNotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<MaintenanceNotification> = emptyList()
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val maintenanceNotificationsRepository: MaintenanceNotificationsRepository
) : ViewModel() {

    val uiState: StateFlow<NotificationsUiState> = maintenanceNotificationsRepository
        .observePending()
        .map { NotificationsUiState(isLoading = false, notifications = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotificationsUiState())

    fun acknowledge(id: Int) {
        viewModelScope.launch {
            maintenanceNotificationsRepository.acknowledge(id)
        }
    }
}
