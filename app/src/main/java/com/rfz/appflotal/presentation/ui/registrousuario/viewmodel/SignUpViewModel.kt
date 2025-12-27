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
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rfz.appflotal.core.util.Connected
import com.rfz.appflotal.core.util.LBEncryptionUtils
import com.rfz.appflotal.data.model.forms.VehicleFormModel
import com.rfz.appflotal.data.model.login.response.AppFlotalMapper
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.registrousuario.screen.SignUpViews
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val mapper: AppFlotalMapper,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private var _signUpUiState: MutableStateFlow<SignUpUiState> = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    var signUpRequestStatus: ApiResult<List<GeneralResponse>?> by mutableStateOf(
        ApiResult.Loading
    )
        private set

    var loginRequestStatus: Result<LoginResponse> by mutableStateOf(
        Result.Loading
    )
        private set

    fun populateListMenus(languageSelected: String) {
        viewModelScope.launch {
            val countriesResponse = catalogUseCase.onGetCountries()
            val sectorsResponse = catalogUseCase.onGetSectors()
            responseHelper(response = countriesResponse) { response ->
                if (response != null) {
                    _signUpUiState.update { currentUiState ->
                        currentUiState.copy(
                            countries = response.associate { it.idCountry to if (languageSelected == "es") it.fldNameEs else it.fldNameEN }
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
                    name = _signUpUiState.value.profileData.name,
                    email = _signUpUiState.value.profileData.email,
                    password = _signUpUiState.value.profileData.password,
                    idCountry = _signUpUiState.value.profileData.country!!.first,
                    idSector = _signUpUiState.value.profileData.industry!!.first,
                    typeVehicle = _signUpUiState.value.vehicleData.vehicleType,
                    plates = _signUpUiState.value.vehicleData.plates
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
        country: Pair<Int, String>?,
        sector: Pair<Int, String>?,
    ): SignUpAlerts {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                profileData = currentUiState.profileData.copy(
                    name = name.trim(),
                    country = country,
                    industry = sector
                ),
                username = username.trim()
            )
        }

        if (name.isEmpty()) return SignUpAlerts.NAME_ALERT

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return SignUpAlerts.EMAIL_ALERT
        else {
            _signUpUiState.update { currentUiState ->
                currentUiState.copy(
                    profileData = currentUiState.profileData.copy(
                        email = email.trim()
                    ),
                    username = getUsername(email.trim()),
                )
            }
        }

        if (password.isEmpty() || password.length < 8)
            return SignUpAlerts.PASSWORD_ALERT
        else {
            _signUpUiState.update { currentUiState ->
                currentUiState.copy(
                    profileData = currentUiState.profileData.copy(
                        password = password.trim(),
                    ),
                )
            }
        }

        if (country == null) return SignUpAlerts.COUNTRY_ALERT

        if (sector == null) return SignUpAlerts.INDUSTRY_ALERT

        return SignUpAlerts.DATAREGISTER_SUCCESSFULY
    }

    fun chargeVehicleData(typeVehicle: String, plates: String): SignUpAlerts {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                vehicleData = VehicleFormModel(
                    vehicleType = typeVehicle.trim(),
                    plates = plates.trim()
                )
            )
        }

        if (typeVehicle.isEmpty())
            return SignUpAlerts.VEHICLE_ALERT

        if (plates.isEmpty())
            return SignUpAlerts.PLATES_ALERT

        return SignUpAlerts.DATAREGISTER_SUCCESSFULY
    }

    fun cleanSignUpData() {
        _signUpUiState.value = SignUpUiState()
    }

    fun changeScreen(currentScreen: SignUpViews) {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                currentScreen = currentScreen
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    fun onLogin(ctx: Context) {
        loginRequestStatus = Result.Loading
        try {
            Firebase.messaging.token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        loginRequestStatus = Result.Failure(Exception("Authentication error"))
                        return@addOnCompleteListener
                    }

                    val token = task.result
                    loginRequest(token)
                    cleanSignUpData()
                }
        } catch (e: Exception) {
            Log.e("SignUpViewModel", "${e.message}")
            loginRequestStatus = Result.Failure(Exception("Error de conexion"))
        }
    }

    private fun loginRequest(token: String){
        viewModelScope.launch {
            val email = signUpUiState.value.profileData.email
            val password = signUpUiState.value.profileData.password
            when (val result = loginUseCase.doLogin(
                LBEncryptionUtils.encrypt(email),
                LBEncryptionUtils.encrypt(password),
                token,
                context
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
        }
    }

    private fun handleLoginResponse(loginResponse: LoginResponse) {
        when (loginResponse.id) {
            200 -> {
                val paymentPlan = when (loginResponse.paymentPlan) {
                    PaymentPlanType.Complete.planName -> PaymentPlanType.Complete
                    PaymentPlanType.OnlyTPMS.planName -> PaymentPlanType.OnlyTPMS
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