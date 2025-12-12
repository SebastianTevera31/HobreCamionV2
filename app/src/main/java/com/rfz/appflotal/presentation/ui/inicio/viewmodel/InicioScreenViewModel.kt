package com.rfz.appflotal.presentation.ui.inicio.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getDateFromNotification
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.domain.database.DeleteTasksUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class InicioScreenViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
) : ViewModel() {

    private val _initialValidationCompleted = MutableLiveData<Boolean>(false)
    val initialValidationCompleted: LiveData<Boolean> = _initialValidationCompleted

    private val _userData = MutableLiveData<AppHCEntity?>()
    val userData: LiveData<AppHCEntity?> = _userData

    var blePermissionGranted by mutableStateOf(false)

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            try {
                val tasks = getTasksUseCase().first()
                _userData.value = tasks.firstOrNull()
                _initialValidationCompleted.value = true
            } catch (e: Exception) {
                _initialValidationCompleted.value = true
            }
        }
    }

    fun deleteUserData() {
        viewModelScope.launch {
            try {
                deleteTasksUseCase()
                _userData.value = null
            } catch (e: Exception) {

            }
        }
    }

    fun updateBlePermissions(hasPermission: Boolean) {
        blePermissionGranted = hasPermission
    }
}