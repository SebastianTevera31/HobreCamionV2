package com.rfz.appflotal.presentation.ui.inicio.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.app_utilities.UserOpinion
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.domain.database.DeleteTasksUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.text.clear

@HiltViewModel
class InicioScreenViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
    private val appUpdateRepository: AppUpdateMessageRepositoryImpl,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _initialValidationCompleted = MutableLiveData<Boolean>(false)
    val initialValidationCompleted: LiveData<Boolean> = _initialValidationCompleted


    val updateMessage = appUpdateRepository.updateFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), null
    )

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

    fun consumeMessage() {
        viewModelScope.launch {
            appUpdateRepository.clear()
        }
    }
}