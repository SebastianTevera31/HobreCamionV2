package com.rfz.appflotal.presentation.ui.login.viewmodel


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.LBEncryptionUtils
import com.rfz.appflotal.data.model.login.response.AppFlotalMapper
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.LoginState
import com.rfz.appflotal.data.model.login.response.LoginState.Error
import com.rfz.appflotal.data.model.login.response.LoginState.Idle
import com.rfz.appflotal.data.model.login.response.LoginState.Loading
import com.rfz.appflotal.data.model.login.response.LoginState.Success
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.repository.vehicle.VehicleRepository
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object NavigateToPermissions : NavigationEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val vehicleRepository: VehicleRepository,
    private val mapper: AppFlotalMapper,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _navigateToHome = MutableLiveData<Triple<Boolean, PaymentPlanType, Boolean>>()

    val navigateToHome: LiveData<Triple<Boolean, PaymentPlanType, Boolean>> = _navigateToHome

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _navigateverifycodeloginScreen = MutableLiveData<Boolean>()
    val navigateverifycodeloginScreen: LiveData<Boolean> = _navigateverifycodeloginScreen

    private val _usuario = MutableLiveData<String>()

    val usuario: LiveData<String> = _usuario

    private val _password = MutableLiveData<String>()

    val password: LiveData<String> = _password

    private val _isLoginEnable = MutableLiveData<Boolean>()

    val isLoginEnable: LiveData<Boolean> = _isLoginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String> = _loginMessage

    private val _loginState = MutableStateFlow<LoginState>(Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isProgressVisible = MutableStateFlow(false)
    val isProgressVisible: StateFlow<Boolean> = _isProgressVisible

    var isUserDataValid: Pair<Boolean, Int?> by mutableStateOf(Pair(true, null))
        private set

    private fun showProgressDialog() {
        _isProgressVisible.value = true
    }

    private fun dismissProgressDialog() {
        _isProgressVisible.value = false
    }

    fun onLoginChanged(usuario: String, password: String) {
        _usuario.value = usuario
        _password.value = password
        _isLoginEnable.value = enableLogin(usuario, password)
    }

    private fun enableLogin(usuario: String, password: String): Boolean {
        return usuario.isNotEmpty() && password.isNotEmpty()
    }

    fun cleanLoginData() {
        _usuario.value = ""
        _password.value = ""
        _loginMessage.value = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    fun onLoginSelected(ctx: Context) {
        showProgressDialog()
        _isLoading.value = true
        _loginState.value = Loading

        try {
            if (!Patterns.EMAIL_ADDRESS.matcher(usuario.value!!).matches()) {
                _loginState.value = Error(ctx.getString(R.string.error_invalid_email))
                _loginMessage.value = ctx.getString(R.string.error_invalid_email)
            } else {
                Firebase.messaging.token
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _loginState.value =
                                Error(context.getString(R.string.error_en_el_servidor))
                            return@addOnCompleteListener
                        }

                        val token = task.result
                        loginRequest(token)
                    }
            }
        } catch (e: Exception) {
            _loginState.value = Error("Unexpected error")
            _loginMessage.value = "Connection error"
        } finally {
            _isLoading.value = false
            dismissProgressDialog()
        }
    }

    private fun loginRequest(fcmToken: String) {
        viewModelScope.launch {

            val user = LBEncryptionUtils.encrypt(usuario.value!!)
            val pass = LBEncryptionUtils.encrypt(password.value!!)

            when (val result = loginUseCase.doLogin(user, pass, fcmToken, context)) {
                is Result.Success -> {
                    handleLoginResponse(
                        result.data,
                        ctx = context
                    )
                }

                is Result.Failure -> {
                    _loginState.value =
                        Error("Unexpected error")
                    _loginMessage.value = "Authentication error"
                }

                Result.Loading -> {}
            }
        }
    }

    private suspend fun handleLoginResponse(loginResponse: LoginResponse, ctx: Context) {
        when (loginResponse.id) {
            200 -> {
                try {
                    onTaskCreated(loginResponse)
                    val paymentPlan = when (loginResponse.paymentPlan) {
                        PaymentPlanType.Complete.planName -> PaymentPlanType.Complete
                        PaymentPlanType.OnlyTPMS.planName -> PaymentPlanType.OnlyTPMS
                        else -> PaymentPlanType.None
                    }
                    _navigateToHome.value =
                        Triple(true, paymentPlan, loginResponse.termsGranted)
                    _loginState.value = Success(loginResponse)
                } catch (e: Exception) {
                    val errorMessage = context.getString(R.string.error_establecer_sesion)
                    _loginState.value = Error(errorMessage)
                    _loginMessage.value = errorMessage
                }
            }

            -100 -> {
                _loginMessage.value = ctx.getString(R.string.credenciales_incorrectas)
                _isLoginEnable.value = true
                _loginState.value =
                    Error(ctx.getString(R.string.credenciales_incorrectas))
            }

            else -> {
                _loginMessage.value =
                    ctx.getString(R.string.error_en_el_servidor, loginResponse.id)
                _loginState.value = Error(ctx.getString(R.string.error_en_el_servidor_only))
            }
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

    fun onNavigateToHomeCompleted() {
        _navigateToHome.value = Triple(false, PaymentPlanType.None, false)
    }

    fun acceptTermsConditions(onSuccess: () -> Unit = {}, onPermissionsGranted: () -> Boolean) {
        viewModelScope.launch {
            val user = getTasksUseCase.invoke().first()

            try {
                if (user.isNotEmpty()) {
                    val result = loginUseCase.doAcceptTermsAndConditions()
                    asyncResponseHelper(result) {
                        addTaskUseCase.updateTermsFlag(user.first().idUser, true)
                        onSuccess()
                        if (onPermissionsGranted()) {
                            _navigationEvent.emit(NavigationEvent.NavigateToPermissions)
                        } else {
                            _navigationEvent.emit(NavigationEvent.NavigateToHome)
                        }
                    }


                }
            } catch (e: Exception) {
                _loginMessage.value = context.getString(R.string.error_aceptar_terminos, e.message)
            }
        }
    }

    fun onNavigateToVerificodeloginComplete() {
        _navigateverifycodeloginScreen.value = false
    }
}
