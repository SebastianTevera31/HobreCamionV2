package com.rfz.appflotal.data.network.service.tpms

import android.util.Log
import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.network.client.tpms.ApiTpmsClient
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ApiTpmsService @Inject constructor(
    private val apiTpmsClient: ApiTpmsClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun postSensorData(sensorRequest: SensorRequest) {
        requestHelper("PostSensorData") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.sendSensorData("bearer $token", sensorRequest)
        }
    }

    suspend fun getDiagramMonitor(idMonitor: Int): ResultApi<List<DiagramMonitorResponse>?> {
        return requestHelper("getDiagramMonitor") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.getDiagramMonitor("bearer $token", idMonitor)
        }
    }

    suspend fun getConfigurationByIdMonitor(idMonitor: Int): ResultApi<List<ConfigurationByIdMonitorResponse>?> {
        return requestHelper {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.getConfigurationByIdMonitor("bearer $token", idMonitor)
        }
    }

    suspend fun checkLogin() {
        try {
            val response = apiTpmsClient.checkLogin()
            if (response.isSuccessful) {
                Log.d("API", "Loggeado correctamente")
            }
        } catch (e: Exception) {
            Log.d("API", "No se completo el loggeo $e")
        }
    }
}