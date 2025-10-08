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
        viewModelScope.launch {
            showProgressDialog()
            _isLoading.value = true
            _loginState.value = Loading

            try {
                if (!Patterns.EMAIL_ADDRESS.matcher(usuario.value!!).matches()) {
                    _loginState.value = Error(ctx.getString(R.string.error_invalid_email))
                    _loginMessage.value = ctx.getString(R.string.error_invalid_email)
                } else {

                    val user = LBEncryptionUtils.encrypt(usuario.value!!)
                    val pass = LBEncryptionUtils.encrypt(password.value!!)

                    when (val result = loginUseCase.doLogin(user, pass, ctx)) {
                        is Result.Success -> {
                            handleLoginResponse(
                                result.data,
                                ctx = ctx
                            )
                        }

                        is Result.Failure -> {
                            _loginState.value =
                                Error(result.exception.message ?: "Unknown error")
                            _loginMessage.value = result.exception.message ?: "Authentication error"
                        }

                        Result.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                _loginState.value = Error(e.message ?: "Unexpected error")
                _loginMessage.value = e.message ?: "Connection error"
            } finally {
                _isLoading.value = false
                dismissProgressDialog()
            }
        }
    }

    private fun handleLoginResponse(loginResponse: LoginResponse, ctx: Context) {
        when (loginResponse.id) {
            200 -> {
                val paymentPlan = when (loginResponse.paymentPlan) {
                    PaymentPlanType.Complete.planName -> PaymentPlanType.Complete
                    PaymentPlanType.OnlyTPMS.planName -> PaymentPlanType.OnlyTPMS
                    else -> PaymentPlanType.None
                }
                onTaskCreated(loginResponse)
                _navigateToHome.value =
                    Pair(true, paymentPlan)
                _loginState.value = Success(loginResponse)
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

    fun onTaskCreated(loginResponse: LoginResponse) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        loginResponse.fecha = dateFormat.format(Date())

        viewModelScope.launch {
            val entity = mapper.fromLoginResponseToEntity(loginResponse)
            addTaskUseCase(entity)
        }
    }

    fun onNavigateToHomeCompleted() {
        _navigateToHome.value = Pair(false, PaymentPlanType.None)
    }

    fun onNavigateToVerificodeloginComplete() {
        _navigateverifycodeloginScreen.value = false
    }
}