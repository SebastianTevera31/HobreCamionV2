package com.rfz.appflotal.presentation.ui.login.viewmodel

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.LBEncryptionUtils
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.data.model.login.response.AppFlotalMapper
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.repository.vehicle.VehicleRepository
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface LoginEvent {
    object NavigateToHome : LoginEvent
    object NavigateToPermissions : LoginEvent
    data class ShowMessage(val message: String) : LoginEvent
}

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState

    data class Error(
        @param:StringRes val message: Int,
        val canRetry: Boolean = true
    ) : LoginUiState

    data class Success(
        val paymentPlan: PaymentPlanType,
        val termsGranted: Boolean
    ) : LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val vehicleRepository: VehicleRepository,
    private val mapper: AppFlotalMapper
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    val isLoginEnabled: StateFlow<Boolean> =
        combine(email, password) { email, pass ->
            email.isNotBlank() && pass.isNotBlank() && _uiState.value is LoginUiState.Loading
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }


    fun cleanLoginData() {
        _email.update { "" }
        _password.update { "" }
    }

    fun onLoginClicked() {
        val email = _email.value

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = LoginUiState.Error(
                message = R.string.error_invalid_email,
                canRetry = false
            )
            return
        }

        _uiState.value = LoginUiState.Loading

        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                _uiState.value = LoginUiState.Error(
                    R.string.error_en_el_servidor
                )
                return@addOnCompleteListener
            }

            loginRequest(task.result)
        }
    }


    private fun loginRequest(fcmToken: String) {
        viewModelScope.launch {
            val user = LBEncryptionUtils.encrypt(_email.value)
            val pass = LBEncryptionUtils.encrypt(_password.value)

            when (val result = loginUseCase.doLogin(user, pass, fcmToken)) {
                is Result.Success -> handleLoginSuccess(result.data)
                is Result.Failure -> _uiState.value =
                    LoginUiState.Error(R.string.auth_error)

                Result.Loading -> _uiState.value = LoginUiState.Loading
            }
        }
    }

    private suspend fun handleLoginSuccess(response: LoginResponse) {
        when (response.id) {
            200 -> {
                onTaskCreated(response)

                val plan = when (response.paymentPlan) {
                    PaymentPlanType.Complete.planName -> PaymentPlanType.Complete
                    PaymentPlanType.OnlyTPMS.planName -> PaymentPlanType.OnlyTPMS
                    else -> PaymentPlanType.None
                }

                _uiState.value = LoginUiState.Success(
                    paymentPlan = plan,
                    termsGranted = response.termsGranted
                )

                _events.emit(
                    if (response.termsGranted)
                        LoginEvent.NavigateToHome
                    else
                        LoginEvent.NavigateToPermissions
                )
            }

            -100 -> _uiState.value = LoginUiState.Error(
                R.string.credenciales_incorrectas
            )

            else -> _uiState.value = LoginUiState.Error(
                R.string.error_en_el_servidor_only
            )
        }
    }

    private suspend fun onTaskCreated(loginResponse: LoginResponse) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        loginResponse.fecha = dateFormat.format(Date())

        val odometerData = vehicleRepository.getLastOdometer(loginResponse.fld_token)
        loginResponse.odometer = odometerData?.lastOdometer ?: 0
        loginResponse.dateLastOdometer = odometerData?.dateOdometer ?: ""

        val entity = mapper.fromLoginResponseToEntity(loginResponse)
        addTaskUseCase(entity)
    }

    fun acceptTermsConditions(
        onSuccess: () -> Unit = {},
        onNavigate: (String) -> Unit,
        onPermissionsGranted: () -> Boolean
    ) {
        viewModelScope.launch {
            val user = getTasksUseCase.invoke().first()

            try {
                if (user.isNotEmpty()) {
                    val result = loginUseCase.doAcceptTermsAndConditions()
                    asyncResponseHelper(result) {
                        addTaskUseCase.updateTermsFlag(user.first().idUser, true)
                        onSuccess()
                        if (onPermissionsGranted()) {
                            onNavigate(NavScreens.PERMISOS)
                        } else {
                            onNavigate(NavScreens.HOME)
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun cleanLoginState() {
        _uiState.update { LoginUiState.Idle }
    }
}