package com.rfz.appflotal.presentation.ui.login.viewmodel


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.rfz.appflotal.core.util.LBEncryptionUtils
import com.rfz.appflotal.data.model.login.response.AppFlotalMapper
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.LoginState
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val mapper: AppFlotalMapper
) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Pair<Boolean, PaymentPlanType>>()
    val navigateToHome: LiveData<Pair<Boolean, PaymentPlanType>> = _navigateToHome

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

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isProgressVisible = MutableStateFlow(false)
    val isProgressVisible: StateFlow<Boolean> = _isProgressVisible

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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    fun onLoginSelected(navController: NavController) {
        _isLoginEnable.value = false
        _loginMessage.value = ""

        viewModelScope.launch {
            showProgressDialog()
            _isLoading.value = true
            _loginState.value = LoginState.Loading

            try {
                val user = LBEncryptionUtils.encrypt(usuario.value!!)
                val pass = LBEncryptionUtils.encrypt(password.value!!)

                when (val result = loginUseCase(user, pass)) {
                    is Result.Success -> {
                        handleLoginResponse(result.data)
                    }

                    is Result.Failure -> {
                        _loginState.value =
                            LoginState.Error(result.exception.message ?: "Unknown error")
                        _loginMessage.value = result.exception.message ?: "Authentication error"
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unexpected error")
                _loginMessage.value = e.message ?: "Connection error"
            } finally {
                _isLoading.value = false
                dismissProgressDialog()
            }
        }
    }

    private fun handleLoginResponse(loginResponse: LoginResponse) {
        when (loginResponse.id) {
            200 -> {
                val paymentPlan = when (loginResponse.paymentPlan) {
                    PaymentPlanType.Complete.planName -> PaymentPlanType.Complete
                    PaymentPlanType.OnlyTpms.planName -> PaymentPlanType.OnlyTpms
                    else -> PaymentPlanType.None
                }
                onTaskCreated(loginResponse)
                _navigateToHome.value =
                    Pair(true, paymentPlan)
                _loginState.value = LoginState.Success(loginResponse)
            }

            -100 -> {
                _loginMessage.value = "Credenciales incorrectas"
                _isLoginEnable.value = true
                _loginState.value = LoginState.Error("Credenciales incorrectas")
            }

            else -> {
                _loginMessage.value = "Error en el servidor: ${loginResponse.id}"
                _loginState.value = LoginState.Error("Error en el servidor")
            }
        }
    }

    fun onTaskCreated(loginResponse: LoginResponse) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        loginResponse.fecha = dateFormat.format(Date())

        viewModelScope.launch {
            val entity = mapper.fromLoginResponseToEntity(loginResponse)
            addTaskUseCase(entity)
        }
    }

    fun onNavigateToHomeComplete() {
        _navigateToHome.value = Pair(false, PaymentPlanType.None)
    }

    fun onNavigateToVerificodeloginComplete() {
        _navigateverifycodeloginScreen.value = false
    }
}