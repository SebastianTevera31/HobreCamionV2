package com.rfz.appflotal.presentation.ui.inicio.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.domain.database.DeleteTasksUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationPermissionState {
    object Granted : NotificationPermissionState()
    object NotRequested : NotificationPermissionState()
    object Denied : NotificationPermissionState()
    object PermanentlyDenied : NotificationPermissionState()
}

@HiltViewModel
class InicioScreenViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()


    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            try {
                val tasks = getTasksUseCase().first()
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = tasks.firstOrNull(),
                        initialValidationCompleted = true
                    )
                }
            } catch (_: Exception) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = null,
                        initialValidationCompleted = true
                    )
                }
            }
        }
    }

    fun deleteUserData() {
        viewModelScope.launch {
            try {
                deleteTasksUseCase()
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = null
                    )
                }
            } catch (_: Exception) {

            }
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun markPermissionsRequested(prefs: SharedPreferences) {
        prefs.edit {
            putBoolean("permissions_requested", true)
        }
    }


    fun werePermissionsRequested(prefs: SharedPreferences): Boolean =
        prefs.getBoolean("permissions_requested", false)

    fun updatePermissionState(state: NotificationPermissionState) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                notificationPermission = state
            )
        }
    }
}

data class MainActivityUiState(
    val adView: AdView? = null,
    val userData: AppHCEntity? = null,
    val initialValidationCompleted: Boolean = false,
    val notificationPermission: NotificationPermissionState = NotificationPermissionState.NotRequested
)