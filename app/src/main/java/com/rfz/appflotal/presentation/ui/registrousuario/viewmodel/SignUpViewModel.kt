package com.rfz.appflotal.presentation.ui.registrousuario.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Connected
import com.rfz.appflotal.data.model.login.response.AppFlotalMapper
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val catalogUseCase: CatalogUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val mapper: AppFlotalMapper
) : ViewModel() {

    private var _signUpUiState: MutableStateFlow<SignUpUiState> = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    var signUpRequestStatus: ApiResult<List<MessageResponse>?> by mutableStateOf(
        ApiResult.Loading
    )
        private set

    var loginRequestStatus: Result<LoginResponse> by mutableStateOf(
        Result.Loading
    )
        private set

    init {
        viewModelScope.launch {
            val countriesResponse = catalogUseCase.onGetCountries()
            val sectorsResponse = catalogUseCase.onGetSectors()
            responseHelper(response = countriesResponse) { response ->
                if (response != null) {
                    _signUpUiState.update { currentUiState ->
                        currentUiState.copy(
                            countries = response.associate { it.idCountry to it.fldNameEN }
                        )
                    }
                }
            }

            responseHelper(response = sectorsResponse) { response ->
                if (response != null) {
                    _signUpUiState.update { currentUiState ->
                        currentUiState.copy(
                            sectors = response.associate { it.idCountry to it.fldSector }
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun signUpUser(ctx: Context, networkAlert: suspend () -> Unit) {
        viewModelScope.launch {
            if (Connected.isConnected(ctx)) {
                signUpRequestStatus = ApiResult.Loading
                signUpRequestStatus = loginUseCase.doRegisterUser(
                    name = _signUpUiState.value.name,
                    username = _signUpUiState.value.username,
                    email = _signUpUiState.value.email,
                    password = _signUpUiState.value.password,
                    idCountry = _signUpUiState.value.country!!.first,
                    idSector = _signUpUiState.value.sector!!.first,
                    typeVehicle = _signUpUiState.value.vehicleType,
                    plates = _signUpUiState.value.plates
                )
            } else networkAlert()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun chargeUserData(
        name: String,
        username: String,
        email: String,
        password: String,
        country: Pair<Int, String>? = Pair(116, "USA"),
        sector: Pair<Int, String>? = Pair(1, "DEVELOPMENT"),
    ): SignUpAlerts {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                name = name.trim(),
                username = username.trim(),
                country = country,
                sector = sector
            )
        }

        if (name.isEmpty()) return SignUpAlerts.Nombre

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return SignUpAlerts.Correo
        else {
            _signUpUiState.update { currentUiState ->
                currentUiState.copy(
                    username = getUsername(email.trim()),
                    email = email.trim(),
                )
            }
        }

        if (password.isEmpty() || password.length < 8)
            return SignUpAlerts.Contrasenia
        else {
            _signUpUiState.update { currentUiState ->
                currentUiState.copy(
                    password = password.trim(),
                )
            }
        }

        if (country == null) return SignUpAlerts.Pais

        if (sector == null) return SignUpAlerts.Sector

        return SignUpAlerts.Registrado
    }

    fun chargeVehicleData(typeVehicle: String, plates: String): SignUpAlerts {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                vehicleType = typeVehicle.trim(),
                plates = plates.trim()
            )
        }

        if (typeVehicle.isEmpty())
            return SignUpAlerts.TypeVehicle

        if (plates.isEmpty())
            return SignUpAlerts.Plates

        return SignUpAlerts.Registrado
    }

    fun cleanSignUpData() {
        _signUpUiState.value = SignUpUiState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    fun onLogin() {
        viewModelScope.launch {
            loginRequestStatus = Result.Loading
            try {
                when (val result = loginUseCase.doLogin(
                    signUpUiState.value.email,
                    signUpUiState.value.password
                )) {
                    is Result.Success -> {
                        handleLoginResponse(result.data)
                        loginRequestStatus = result
                    }

                    is Result.Failure -> {
                        loginRequestStatus = Result.Failure(Exception("Authentication error"))
                    }

                    Result.Loading -> Result.Loading
                }
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "${e.message}")
                loginRequestStatus = Result.Failure(Exception("Connection error"))
            }
        }
    }

    private fun handleLoginResponse(loginResponse: LoginResponse) {
        when (loginResponse.id) {
            200 -> {
                val paymentPlan = when (loginResponse.paymentPlan) {
                    PaymentPlanType.Complete.planName -> PaymentPlanType.Complete
                    PaymentPlanType.OnlyTpms.planName -> PaymentPlanType.OnlyTpms
                    PaymentPlanType.Free.planName -> PaymentPlanType.Free
                    else -> PaymentPlanType.None
                }

                _signUpUiState.update { currentUiState ->
                    currentUiState.copy(
                        paymentPlan = paymentPlan
                    )
                }

                // Registra usuario en la base de datos
                onTaskCreated(loginResponse)
            }

            -100 -> {
                Log.e("SignUpViewModel", "Credenciales incorrectas")
            }

            else -> {
                Log.e("SignUpViewModel", "Error en el servidor: ${loginResponse.id}")
            }
        }
    }

    private fun onTaskCreated(loginResponse: LoginResponse) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        loginResponse.fecha = dateFormat.format(Date())

        viewModelScope.launch {
            val entity = mapper.fromLoginResponseToEntity(loginResponse)
            addTaskUseCase(entity)
        }
    }

    private fun getUsername(email: String): String {
        val username = email.split("@")[0]
        return if (username.length > 8) username.substring(0, 7)
        else username
    }
}