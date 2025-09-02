package com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.login.LoginUseCase
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpAlerts
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
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private var _updateUserUiState = MutableStateFlow(UpdateUserUiState())
    val updateUserUiState = _updateUserUiState.asStateFlow()

    var updateUserStatus: ApiResult<List<MessageResponse>?> by mutableStateOf(ApiResult.Loading)
        private set

    fun fetchUserData() {
        viewModelScope.launch {
            val driverData = getTasksUseCase().first()[0]

            _updateUserUiState.update { currentUiState ->
                currentUiState.copy(
                    userData = driverData.toUserData(),
                    newData = driverData.toUserData(),
                    isNewData = false
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
                            countries = response.associate { it.idCountry to it.fldNameEN }
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
                        newData = currentUiState.newData.copy(
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
                        newData = currentUiState.newData.copy(
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
                newData = currentUiState.newData.copy(
                    name = name.trim(),
                    username = username,
                    email = email.trim(),
                    password = password.trim(),
                    country = country,
                    industry = industry
                )
            )
        }
        verifyIsNewData()

        if (name.isEmpty()) showMessage(context, SignUpAlerts.NAME_ALERT.message)

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            showMessage(context, SignUpAlerts.EMAIL_ALERT.message)

//        if (password.isEmpty() || password.length < 8)
//            showMessage(context, SignUpAlerts.PASSWORD_ALERT.message)
    }

    fun updateVehicleData(typeVehicle: String, plates: String, context: Context) {
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                newData = currentUiState.newData.copy(
                    typeVehicle = typeVehicle.trim(),
                    plates = plates.trim()
                )
            )
        }
        if (typeVehicle.isEmpty())
            showMessage(context, SignUpAlerts.VEHICLE_ALERT.message)

        if (plates.isEmpty())
            showMessage(context, SignUpAlerts.PLATES_ALERT.message)

        verifyIsNewData()
    }

    fun verifyIsNewData() {
        val newData = _updateUserUiState.value.newData
        val currentData = _updateUserUiState.value.userData
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                isNewData = newData != currentData
            )
        }
    }

    fun saveUserData() {
        updateUserStatus = ApiResult.Loading
        viewModelScope.launch {
            val response = loginUseCase.doUpdateUser(
                name = _updateUserUiState.value.newData.name,
                username = _updateUserUiState.value.newData.username,
                email = _updateUserUiState.value.newData.email,
                password = _updateUserUiState.value.newData.password,
                idCountry = _updateUserUiState.value.newData.country?.first ?: 0,
                idSector = _updateUserUiState.value.newData.industry?.first ?: 0,
                typeVehicle = _updateUserUiState.value.newData.typeVehicle,
                plates = _updateUserUiState.value.newData.plates
            )

            responseHelper(response = response) { data ->
                updateUserStatus = ApiResult.Success(data)
                _updateUserUiState.update { currentUiState ->
                    currentUiState.copy(
                        isNewData = false
                    )
                }
            }

            if (updateUserStatus != ApiResult.Loading || updateUserStatus != ApiResult.Error()) {
                addTaskUseCase.updateUserData(
                    idUser = _updateUserUiState.value.newData.idUser,
                    fldName = _updateUserUiState.value.newData.name,
                    fldEmail = _updateUserUiState.value.newData.email,
                    vehiclePlates = _updateUserUiState.value.newData.plates,
                    country = _updateUserUiState.value.newData.country?.first ?: 0,
                    industry = _updateUserUiState.value.newData.industry?.first ?: 0,
                    vehicleType = _updateUserUiState.value.newData.typeVehicle
                )
            }
        }
    }

    fun cleanUpdateUserStatus() {
        updateUserStatus = ApiResult.Loading
    }

    private fun showMessage(context: Context, message: Int) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show()
    }
}