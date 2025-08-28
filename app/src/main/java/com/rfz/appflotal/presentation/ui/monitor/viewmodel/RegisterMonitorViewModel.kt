package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RegisterMonitorMessage(val message: String) {
    EMPTY_MONITOR("Ingrese la MAC del monitor"),
    EMPTY_CONFIGURATION("Seleccione el tipo de monitor"),
    REGISTERED("Monitor registrado correctamente"),
    UNKNOWN_ERROR("Error desconocido"),
    NO_DATA("Sin datos")
}

@HiltViewModel
class RegisterMonitorViewModel @Inject constructor(
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private var _configurationsList =
        MutableStateFlow<Map<Int, String>>(emptyMap())
    val configurationList = _configurationsList.asStateFlow()

    private var _registeredMonitorState = MutableStateFlow<ApiResult<Int>>(ApiResult.Loading)
    val registeredMonitorState = _registeredMonitorState.asStateFlow()

    init {
        loadConfigurations()
    }

    fun loadConfigurations() {
        viewModelScope.launch {
            val response = apiTpmsUseCase.doGetConfigurations()
            responseHelper(response = response) { result ->
                if (result != null) {
                    val products = result
                        .filterNot { it.idConfiguration == 2 }
                        .associate {
                            it.idConfiguration to it.fldDescription.replace("BASE", "TALON")
                        }

                    _configurationsList.value = products
                }
            }
        }
    }

    fun registerMonitor(mac: String, configurationSelected: Pair<Int, String>?, context: Context) {
        _registeredMonitorState.value = ApiResult.Loading

        if (configurationSelected == null) {
            showAlert(context, RegisterMonitorMessage.EMPTY_CONFIGURATION.message)
            return
        }

        if (mac.isEmpty()) {
            showAlert(context, RegisterMonitorMessage.EMPTY_MONITOR.message)
            return
        }

        viewModelScope.launch {
            val userData = getTasksUseCase().first()[0]

            val response = apiTpmsUseCase.doPostCrudMonitor(
                idMonitor = 0,
                fldMac = mac,
                fldDate = getCurrentDate(),
                idVehicle = userData.idVehicle,
                idConfiguration = configurationSelected.first
            )

            responseHelper(
                response = response
            ) { result ->
                if (!result.isNullOrEmpty()) {
                    val fields = result[0].message.split(":")
                    if (fields.size == 2) {
                        val idMonitor = fields[1].trim().toIntOrNull()
                        if (idMonitor != null) {
                            updateMonitorDataDB(
                                idMonitor,
                                mac,
                                "BASE ${configurationSelected.first}", userData.id_user
                            )
                            showAlert(context, RegisterMonitorMessage.REGISTERED.message)
                            _registeredMonitorState.value = ApiResult.Success(data = idMonitor)
                        }
                    } else {
                        showAlert(context, result[0].message)
                    }

                } else {
                    showAlert(context, RegisterMonitorMessage.UNKNOWN_ERROR.message)
                }
            }
        }
    }

    fun cleanMonitorRegistrationData() {
        _registeredMonitorState.value = ApiResult.Loading
    }

    private fun updateMonitorDataDB(
        idMonitor: Int,
        mac: String,
        baseConfiguration: String,
        idUser: Int
    ) {
        viewModelScope.launch {
            getTasksUseCase.updateMonitor(idMonitor, mac, baseConfiguration, idUser)
        }
    }

    private fun showAlert(ctx: Context, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
    }
}