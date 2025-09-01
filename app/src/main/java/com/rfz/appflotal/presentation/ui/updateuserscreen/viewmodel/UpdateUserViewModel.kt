package com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.database.AddTaskUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
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
    private val addTaskUseCase: AddTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private var _updateUserUiState = MutableStateFlow(UpdateUserUiState())
    val updateUserUiState = _updateUserUiState.asStateFlow()

    fun fetchUserData() {
        viewModelScope.launch {
            val countriesResponse = catalogUseCase.onGetCountries()
            val sectorsResponse = catalogUseCase.onGetSectors()
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
                            industries = response.associate { it.idCountry to it.fldSector }
                        )
                    }
                }
            }
            val driverData = getTasksUseCase().first()[0]
            _updateUserUiState.update { currentUiState ->
                currentUiState.copy(
                    userData = driverData.toUserData(),
                    newData = driverData.toUserData()
                )
            }
        }
    }

    fun updateUserData(
        name: String,
        username: String,
        email: String,
        password: String,
        country: Pair<Int, String>?,
        industry: Pair<Int, String>?
    ) {
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                newData = currentUiState.userData.copy(
                    name = name,
                    username = username,
                    email = email,
                    password = password,
                    country = country,
                    industry = industry
                )
            )
        }
    }

    fun updateVehicleData(typeVehicle: String, plates: String) {
        _updateUserUiState.update { currentUiState ->
            currentUiState.copy(
                newData = currentUiState.userData.copy(
                    typeVehicle = typeVehicle,
                    plates = plates
                )
            )
        }
    }

    fun saveUserData() {

        _updateUserUiState.value = UpdateUserUiState()
    }

}