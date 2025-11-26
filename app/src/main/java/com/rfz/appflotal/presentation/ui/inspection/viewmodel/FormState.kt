package com.rfz.appflotal.presentation.ui.inspection.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.utils.toIntOrError

@Stable
class InspectionFormState(
    odometer: String = "",
    temperature: String = "",
    pressureMeasured: String = "",
    adjustedPressure: String = "",
    treadDepth1: String = "0",
    treadDepth2: String = "0",
    treadDepth3: String = "0",
    treadDepth4: String = "0",
    selectedReportId: String? = null
) {
    var odometer by mutableStateOf(odometer)
    var temperature by mutableStateOf(temperature)
    var pressureMeasured by mutableStateOf(pressureMeasured)
    var adjustedPressure by mutableStateOf(adjustedPressure)
    var treadDepth1 by mutableStateOf(treadDepth1)
    var treadDepth2 by mutableStateOf(treadDepth2)
    var treadDepth3 by mutableStateOf(treadDepth3)
    var treadDepth4 by mutableStateOf(treadDepth4)
    var selectedReportId by mutableStateOf(selectedReportId)

    // Errores por campo (null = sin error)

    var selectedReportIdError by mutableStateOf<Int?>(null)
    var odometerError by mutableStateOf<Int?>(null)
    var temperatureError by mutableStateOf<Int?>(null)
    var pressureMeasuredError by mutableStateOf<Int?>(null)
    var adjustedPressureError by mutableStateOf<Int?>(null)
    var treadDepth1Error by mutableStateOf<Int?>(null)
    var treadDepth2Error by mutableStateOf<Int?>(null)
    var treadDepth3Error by mutableStateOf<Int?>(null)
    var treadDepth4Error by mutableStateOf<Int?>(null)
    var oneTreadDepthAtLeast by mutableStateOf<Boolean?>(false)

    fun validate(): Boolean {
        val (reportInt, reportErr) = if (selectedReportId != null) Pair(
            selectedReportId,
            null
        ) else Pair(
            null,
            R.string.requerido
        )
        selectedReportIdError = reportErr

        val (odoInt, odoErr) = odometer.toIntOrError()
        odometerError = odoErr

        val (tempInt, tempErr) = temperature.toIntOrError()
        temperatureError = tempErr

        val (pressInt, pressErr) = pressureMeasured.toIntOrError()
        pressureMeasuredError = pressErr

        val (adjInt, adjErr) = adjustedPressure.toIntOrError()
        adjustedPressureError = adjErr

        val (td1Int, td1Err) = treadDepth1.toIntOrError()
        treadDepth1Error = td1Err

        val (td2Int, td2Err) = treadDepth2.toIntOrError()
        treadDepth2Error = td2Err

        val (td3Int, td3Err) = treadDepth3.toIntOrError()
        treadDepth3Error = td3Err

        val (td4Int, td4Err) = treadDepth4.toIntOrError()
        treadDepth4Error = td4Err

        oneTreadDepthAtLeast = treadDeptError(
            td1Int,
            td4Int
        )

        // Si alguno dio error (no null), no es válido
        return listOf(
            odoErr, tempErr, pressErr, adjErr,
            td1Err, td2Err, td3Err, td4Err, reportErr, oneTreadDepthAtLeast
        ).all { it == null } &&
                // Y además todos pudieron convertirse:
                listOf(
                    reportInt,
                    odoInt,
                    tempInt,
                    pressInt,
                    adjInt,
                    td1Int,
                    td2Int,
                    td3Int,
                    td4Int,
                ).all { it != null }
    }

    fun toUiOrNull(): InspectionUi? {
        if (!validate()) return null

        return InspectionUi(
            odometer = odometer.toInt(),
            temperature = temperature.toInt(),
            pressure = pressureMeasured.toInt(),
            adjustedPressure = adjustedPressure.toInt(),
            treadDepth1 = treadDepth1.toFloat(),
            treadDepth2 = treadDepth2.toFloat(),
            treadDepth3 = treadDepth3.toFloat(),
            treadDepth4 = treadDepth4.toFloat(),
            reportId = selectedReportId
        )
    }

    companion object {
        // Guardamos como lista de Strings para Saver (fácil y eficiente).
        val Saver: Saver<InspectionFormState, Any> = listSaver(
            save = {
                listOf(
                    it.odometer, it.temperature, it.pressureMeasured, it.adjustedPressure,
                    it.treadDepth1, it.treadDepth2, it.treadDepth3, it.treadDepth4,
                    it.selectedReportId
                )
            },
            restore = { list ->
                InspectionFormState(
                    odometer = list.getOrNull(0) ?: "",
                    temperature = list.getOrNull(1) ?: "",
                    pressureMeasured = list.getOrNull(2) ?: "",
                    adjustedPressure = list.getOrNull(3) ?: "",
                    treadDepth1 = list.getOrNull(4) ?: "",
                    treadDepth2 = list.getOrNull(5) ?: "",
                    treadDepth3 = list.getOrNull(6) ?: "",
                    treadDepth4 = list.getOrNull(7) ?: "",
                    selectedReportId = list.getOrNull(8).orEmpty().ifBlank { null }
                )
            }
        )
    }
}

@Composable
fun rememberInspectionFormState(
    initialTemperature: Int,
    initialPressure: Int
): InspectionFormState {
    return rememberSaveable(saver = InspectionFormState.Saver) {
        InspectionFormState(
            temperature = initialTemperature.toString(),
            pressureMeasured = initialPressure.toString()
        )
    }
}