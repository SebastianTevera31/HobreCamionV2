package com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.UnitProvider
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.database.UpdateVehicleDataUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpAlerts
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val catalogUseCase: CatalogUseCase,
    private val loginUseCase: LoginUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val wifiUseCase: WifiUseCase,
    private val observeTemperatureUnitUseCase: ObserveTemperatureUnitUseCase,
    private val observePressureUnitUseCase: ObservePressureUnitUseCase,
    private val observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
    private val updateVehicleDataUseCase: UpdateVehicleDataUseCase,
) : ViewModel() {

    private var _updateUserUiState: MutableStateFlow<UpdateUserUiState> =
        MutableStateFlow(UpdateUserUiState())
    val updateUserUiState = _updateUserUiState.asStateFlow()

    var updateUserStatus: ApiResult<List<GeneralResponse>?> by mutableStateOf(ApiResult.Loading)
        private set

    private val _wifiStatus: MutableStateFlow<NetworkStatus> =
        MutableStateFlow(NetworkStatus.Connected)

    val wifiStatus = _wifiStatus.asStateFlow()

    init {
        viewModelScope.launch {
            wifiUseCase().collect { status ->
                _wifiStatus.update { status }
            }
        }
    }

    fun fetchUserData(selectedLanguage: String) {
        viewModelScope.launch {
            val driverData = getTasksUseCase().first()[0]
            val currentPressureUnit = observePressureUnitUseCase().first()
            val currentTemperatureUnit = observeTemperatureUnitUseCase().first()
            val currentOdometerUnit = observeOdometerUnitUseCase().first()

            val userData = driverData.toUserData()
            val vehicleData = driverData.toVehicleData().copy(
                temperatureUnit = currentTemperatureUnit,
                pressureUnit = currentPressureUnit,
                odometerUnit = currentOdometerUnit
            )


            _updateUserUiState.update { currentUiState ->
                currentUiState.copy(
                    idVehicle = driverData.idVehicle,
                    userData = userData,
                    vehicleData = vehicleData,
                    newUserData = userData,
                    newVehicleData = vehicleData,
                    isNewUserData = false,
                    isNewVehicleData = false
                )
            }

            val countriesResponse = catalogUseCase.onGetCountries()
            val sectorsResponse = catalogUseCase.onGetSectors()

            val idCountry = driverData.country
            val idIndustry = driverData.industry

            responseHelper(response = countriesResponse) { response ->
                if (response != null) {
                    _updateUserUiState.update { currentUiState ->
                        currentUiState.copy(
                            countries = response.associate { it.idCountry to if (selectedLanguage == "en") it.fldNameEN else it.fldNameEs }
                        )
                    }
                }
            }

            responseHelper(response = sectorsResponse) { response ->
                if (response != null) {
                    _updateUserUiState.update { currentUiState ->
                        currentUiState.copy(
                            industries = response.associate { it.idCountry to it.fldSector },
                        )
                    }
                }
            }

            getDefaultCountry(idCountry)
            getDefaultIndustry(idIndustry)
        }
    }

    fun getDefaultCountry(idCountry: Int) {
        val countries = _updateUserUiState.value.countries
        if (countries.isNotEmpty()) {
            val currentCountry = countries.filterKeys { idCountry == it }
                .map { Pair(it.key, it.value) }
            if (currentCountry.isNotEmpty()) {
                _updateUserUiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = currentUiState.userData.copy(
                            country = currentCountry[0],
                        ),
                        newUserData = currentUiState.newUserData.copy(
                            country = currentCountry[0]
                        )
                    )
                }
            }
        }
    }

    fun getDefaultIndustry(idIndustry: Int) {
        val industries = _updateUserUiState.value.industries
        if (industries.isNotEmpty()) {
            val currentIndustry = industries.filterKeys { idIndustry == it }
                .map { Pair(it.key, it.value) }
            if (currentIndustry.isNotEmpty()) {
                _updateUserUiState.update { currentUiState ->
                    currentUiState.copy(
                        userData = currentUiState.userData.copy(
                            industry = currentIndustry[0],
                        ),
                        newUserData = currentUiState.newUserData.copy(
                            industry = currentIndustry[0]
                        )
                    )
                }
            }
        }
    }

    fun updateUserData(
        name: String,
        username: String = "",
        email: String,
        password: String,
        country: Pair<Int, String>?,
        industry: Pair<Int, String>?,
        context: Context
    ) {
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                newUserData = currentUiState.newUserData.copy(
                    name = name,
                    username = username,
                    email = email,
                    password = password,
                    country = country,
                    industry = industry
                )
            )
        }
        verifyIsNewUserData()

        if (name.isEmpty()) showMessage(context, SignUpAlerts.NAME_ALERT.message)

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            showMessage(context, SignUpAlerts.EMAIL_ALERT.message)

//        if (password.isEmpty() || password.length < 8)
//            showMessage(context, SignUpAlerts.PASSWORD_ALERT.message)
    }

    fun updateVehicleData(
        typeVehicle: String,
        plates: String,
        temperatureUnit: UnitProvider,
        pressureUnit: UnitProvider,
        odometerUnit: UnitProvider,
        context: Context
    ) {
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                newVehicleData = currentUiState.newVehicleData.copy(
                    typeVehicle = typeVehicle,
                    plates = plates,
                    temperatureUnit = temperatureUnit,
                    pressureUnit = pressureUnit,
                    odometerUnit = odometerUnit
                )
            )
        }
        if (typeVehicle.isEmpty())
            showMessage(context, SignUpAlerts.VEHICLE_ALERT.message)

        if (plates.isEmpty())
            showMessage(context, SignUpAlerts.PLATES_ALERT.message)

        verifyIsNewVehicleData()
    }

    private fun verifyIsNewVehicleData() {
        val newData = _updateUserUiState.value.newVehicleData
        val currentData = _updateUserUiState.value.vehicleData
        val isNewVehicleData = listOf(
            newData.typeVehicle != currentData.typeVehicle ||
                    newData.plates != currentData.plates ||
                    newData.pressureUnit != currentData.pressureUnit ||
                    newData.temperatureUnit != currentData.temperatureUnit ||
                    newData.odometerUnit != currentData.odometerUnit
        ).any { it }
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                isNewVehicleData = isNewVehicleData
            )
        }
    }

    private fun verifyIsNewUserData() {
        val newData = _updateUserUiState.value.newUserData
        val currentData = _updateUserUiState.value.userData
        val isNewUserData = listOf(
            newData.name != currentData.name,
            newData.email != currentData.email,
            newData.password != currentData.password,
            newData.country != currentData.country,
            newData.industry != currentData.industry
        ).any { it }
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                isNewUserData = isNewUserData
            )
        }
    }

    fun saveUserData() {
        updateUserStatus = ApiResult.Loading
        viewModelScope.launch {
            if (_updateUserUiState.value.isNewUserData) {
                updateOperatorData()
            }

            if (_updateUserUiState.value.isNewVehicleData) {
                updateVehicleData()
            }
        }
    }

    private suspend fun updateOperatorData() {
        val response = loginUseCase.doUpdateUser(
            name = _updateUserUiState.value.newUserData.name.trim(),
            email = _updateUserUiState.value.newUserData.email.trim(),
            password = _updateUserUiState.value.newUserData.password.trim(),
            idCountry = _updateUserUiState.value.newUserData.country?.first ?: 0,
            idSector = _updateUserUiState.value.newUserData.industry?.first ?: 0,
        )

        asyncResponseHelper(
            response = response,
            onError = { updateUserStatus = ApiResult.Error() }
        ) { data ->
            updateUserStatus = ApiResult.Success(data)
            _updateUserUiState.update { currentUiState ->
                currentUiState.copy(
                    isNewUserData = false,
                )
            }

            if (updateUserStatus != ApiResult.Loading || updateUserStatus != ApiResult.Error()) {
                addTaskUseCase.updateUserData(
                    idUser = _updateUserUiState.value.newUserData.idUser,
                    fldName = _updateUserUiState.value.newUserData.name,
                    fldEmail = _updateUserUiState.value.newUserData.email,
                    country = _updateUserUiState.value.newUserData.country?.first ?: 0,
                    industry = _updateUserUiState.value.newUserData.industry?.first ?: 0,
                )
            }
        }
    }

    private suspend fun updateVehicleData() {
        val newData = _updateUserUiState.value.newVehicleData
        val currentData = _updateUserUiState.value.vehicleData

        newData.temperatureUnit != currentData.temperatureUnit ||
                newData.odometerUnit != currentData.odometerUnit

        val response = updateVehicleDataUseCase(
            idUser = _updateUserUiState.value.newUserData.idUser,
            vehicleId = _updateUserUiState.value.idVehicle,
            vehicleType = _updateUserUiState.value.newVehicleData.typeVehicle,
            vehiclePlates = _updateUserUiState.value.newVehicleData.plates,
            switchTemperature = newData.temperatureUnit != currentData.temperatureUnit,
            switchPressure = newData.pressureUnit != currentData.pressureUnit,
            switchOdometer = newData.odometerUnit != currentData.odometerUnit
        )

        response.onSuccess { data ->
            updateUserStatus = ApiResult.Success(listOf(data))
            _updateUserUiState.update { currentUiState ->
                currentUiState.copy(
                    isNewUserData = false,
                )
            }

            if (updateUserStatus != ApiResult.Loading || updateUserStatus != ApiResult.Error()) {
                addTaskUseCase.updateUserData(
                    idUser = _updateUserUiState.value.newUserData.idUser,
                    fldName = _updateUserUiState.value.newUserData.name,
                    fldEmail = _updateUserUiState.value.newUserData.email,
                    country = _updateUserUiState.value.newUserData.country?.first ?: 0,
                    industry = _updateUserUiState.value.newUserData.industry?.first ?: 0,
                )
            }
        }.onFailure {
            updateUserStatus = ApiResult.Error()
        }
    }

    fun cleanUpdateUserStatus() {
        updateUserStatus = ApiResult.Loading
    }

    private fun showMessage(context: Context, message: Int) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show()
    }
}