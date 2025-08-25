package com.rfz.appflotal.presentation.ui.registrousuario.viewmodel

import android.content.Context
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Connected
import com.rfz.appflotal.core.util.LBEncryptionUtils
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val loginUseCase: LoginUseCase) :
    ViewModel() {
    private var _signUpUiState: MutableStateFlow<SignUpUiState> = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    var signUpRequestStatus: ApiResult<List<MessageResponse>?> by mutableStateOf(
        ApiResult.Loading
    )
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun signUpUser(ctx: Context, networkAlert: suspend () -> Unit) {
        viewModelScope.launch {
            if (Connected.isConnected(ctx)) {
                signUpRequestStatus = ApiResult.Loading
                signUpRequestStatus = loginUseCase.doRegisterUser(
                    name = _signUpUiState.value.name,
                    username = _signUpUiState.value.username,
                    email = _signUpUiState.value.email,
                    password = signUpUiState.value.password,
                    idCountry = _signUpUiState.value.country!!.first,
                    idSector = _signUpUiState.value.sector!!.first,
                    typeVehicle = _signUpUiState.value.vehicleType,
                    plates = _signUpUiState.value.plates
                )
            } else networkAlert()
        }
    }

    fun chargeUserData(
        name: String,
        username: String,
        email: String,
        password: String,
        country: Pair<Int, String>? = Pair(1, "USA"),
        sector: Pair<Int, String>? = Pair(1, "DEVELOPMENT"),
    ): String? {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                name = name.trim(),
                username = username.trim(),
                email = email.trim(),
                password = password.trim(),
                country = country,
                sector = sector
            )
        }

        if (name.isEmpty()) return "Introduzca su nombre"

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Introduzca un correo valido"

        if (password.isEmpty() || password.length < 8)
            return "Introduzca una contraseña de minimo 8 caracteres"

        if (country == null) return "Seleccione un país"

        if (sector == null) return "Seleccione un sector"

        return null
    }

    fun chargeVehicleData(typeVehicle: String, plates: String): String? {
        _signUpUiState.update { currentUiState ->
            currentUiState.copy(
                vehicleType = typeVehicle.trim(),
                plates = plates.trim()
            )
        }

        if (typeVehicle.isEmpty())
            return "Especifique el tipo de vehiculo"

        if (plates.isEmpty())
            return "Introduzca las placas del vehiculo"

        return null
    }

    fun cleanSignUpData() {
        _signUpUiState = MutableStateFlow(SignUpUiState())
    }
}