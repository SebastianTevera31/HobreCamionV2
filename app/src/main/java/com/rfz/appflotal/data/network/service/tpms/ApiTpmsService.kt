package com.rfz.appflotal.data.network.service.tpms

import android.util.Log
import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.client.tpms.ApiTpmsClient
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ApiTpmsService @Inject constructor(
    private val apiTpmsClient: ApiTpmsClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun postSensorData(sensorRequest: SensorRequest): ApiResult<List<TpmsResponse>?> {
        return requestHelper("PostSensorData") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.sendSensorData("bearer $token", sensorRequest)
        }
    }

    suspend fun getDiagramMonitor(idMonitor: Int): ApiResult<List<DiagramMonitorResponse>?> {
        return requestHelper("getDiagramMonitor") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.getDiagramMonitor("bearer $token", idMonitor)
        }
    }

    suspend fun getConfigurationByIdMonitor(idMonitor: Int): ApiResult<List<ConfigurationByIdMonitorResponse>?> {
        return requestHelper("getConfigurationByIdMonitor") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.getConfigurationByIdMonitor("bearer $token", idMonitor)
        }
    }

    suspend fun getMonitorTireByDate(
        monitorId: Int,
        position: String,
        fldDate: String
    ): ApiResult<List<MonitorTireByDateResponse>?> {
        return requestHelper("getMonitorTireByDate") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.getMonitorTireByDate("bearer $token", monitorId, fldDate, position)
        }
    }

    suspend fun getPositionCoordinates(
        monitorId: Int
    ): ApiResult<List<PositionCoordinatesResponse>?> {
        return requestHelper("getPositionCoordinates") {
            val token = getTasksUseCase().first()[0].fld_token
            apiTpmsClient.getPositionCoordinates("bearer $token", monitorId)
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