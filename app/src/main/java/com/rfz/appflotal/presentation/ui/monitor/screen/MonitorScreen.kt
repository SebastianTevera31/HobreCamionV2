package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel

@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel,
    navController: NavController,
    paymentPlan: PaymentPlanType,
    modifier: Modifier = Modifier,
) {
    val monitorUiState = monitorViewModel.monitorUiState.collectAsState()
    val positionsUiState = monitorViewModel.positionsUiState.collectAsState()
    val monitorTireUiState = monitorViewModel.monitorTireUiState.collectAsState()

    Scaffold(topBar = { if (paymentPlan == PaymentPlanType.Complete) MonitorTopBar { navController.popBackStack() } }) { innerPadding ->
        var selectedTab by remember { mutableIntStateOf(R.string.diagrama) }

        Surface {
            Column(
                modifier = modifier
                    .background(Color("#EDF0F8".toColorInt()))
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // Navegacion entre Diagrama y Posiciones
                TabRow(
                    selectedTabIndex = if (selectedTab == R.string.diagrama) 0 else 1,
                    containerColor = Color(0xFF3F51B5), contentColor = Color.White,
                ) {
                    Tab(
                        selected = selectedTab == R.string.diagrama,
                        onClick = { selectedTab = R.string.diagrama }) {
                        Text(stringResource(R.string.diagrama), modifier = Modifier.padding(16.dp))
                    }
                    Tab(
                        selected = selectedTab == R.plurals.posicion_tag,
                        onClick = {
                            monitorViewModel.getListSensorData()
                            selectedTab = R.plurals.posicion_tag
                        }) {
                        Text(
                            pluralStringResource(R.plurals.posicion_tag, 2),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }


                if (selectedTab == R.string.diagrama) {
                    // Obtener coordenadas del diagrama
                    monitorViewModel.getDiagramCoordinates()

                    DiagramaMonitorScreen(
                        imageUrl = monitorUiState.value.chassisImageUrl,
                        wheel = monitorUiState.value.wheel,
                        temperature = monitorUiState.value.temperature.first,
                        pressure = monitorUiState.value.pression.first,
                        timestamp = monitorUiState.value.timestamp,
                        temperatureStatus = monitorUiState.value.temperature.second,
                        pressionStatus = monitorUiState.value.pression.second,
                        numWheels = monitorUiState.value.numWheels,
                        alertTires = monitorUiState.value.wheelsWithAlert,
                        getSensorData = { sensorId -> monitorViewModel.getSensorDataByWheel(sensorId) },
                        coordinates = monitorUiState.value.coordinateList,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    var positionOptionSelected by remember { mutableIntStateOf(R.string.recientes) }

                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Navegacion Recientes y Buscar
                        NavPositionMonitorScreen(
                            numWheels = monitorUiState.value.numWheels,
                            onSensorData = { sensorSelected, dateSelected ->
                                monitorViewModel.getTireDataByDate(
                                    sensorSelected,
                                    dateSelected
                                )
                            },
                            onPositionOptionSelected = { option ->
                                positionOptionSelected = option
                            }
                        )

                        if (positionOptionSelected == R.string.recientes) {
                            val positionData = positionsUiState.value
                            when (positionData) {
                                is ResultApi.Success -> {
                                    val data: List<DiagramMonitorResponse>? = positionData.data
                                    CurrentPositionDataView(
                                        sensorDataList = monitorViewModel.convertToTireData(data),
                                        isOnSearch = false,
                                    )
                                }

                                is ResultApi.Error -> {}
                                is ResultApi.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        } else {
                            val monitorTireData = monitorTireUiState.value
                            when (monitorTireData) {
                                is ResultApi.Success -> {
                                    val data: List<MonitorTireByDateResponse>? =
                                        monitorTireData.data
                                    CurrentPositionDataView(
                                        sensorDataList = data,
                                        isOnSearch = true
                                    )
                                }

                                is ResultApi.Error -> {}
                                is ResultApi.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavPositionMonitorScreen(
    numWheels: Int,
    onSensorData: (String, String) -> Unit,
    onPositionOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchRecords by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 16.dp,
        modifier = modifier,
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Button(
                    onClick = {
                        onPositionOptionSelected(R.string.recientes)
                        showSearchRecords = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color("#2E3192".toColorInt())),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.recientes))
                }

                Button(
                    onClick = {
                        onPositionOptionSelected(R.string.buscar)
                        showSearchRecords = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color("#2E3192".toColorInt())),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.buscar))
                }
            }

            if (showSearchRecords) {
                PositionFilterView(
                    numWheels = numWheels,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) { wheelSelected, dateSelected -> onSensorData(wheelSelected, dateSelected) }
            }
        }
    }
}
