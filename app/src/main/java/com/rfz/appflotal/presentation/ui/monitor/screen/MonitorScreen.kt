package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.presentation.ui.loading.screen.LoadingScreen
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel

@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel, modifier: Modifier = Modifier,
    navController: NavController
) {
    val monitorUiState = monitorViewModel.monitorUiState.collectAsState()
    val positionsUiState = monitorViewModel.positionsUiState.collectAsState()

    Scaffold(topBar = { MonitorTopBar { navController.popBackStack() } }) { innerPadding ->
        var selectedTab by remember { mutableIntStateOf(R.string.diagrama) }

        Surface {
            Column(
                modifier = modifier
                    .background(Color("#EDF0F8".toColorInt()))
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

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
                    DiagramaMonitorScreen(
                        imageUrl = monitorUiState.value.chassisImageUrl,
                        wheel = monitorUiState.value.wheel,
                        temperature = monitorUiState.value.temperature.first,
                        pressure = monitorUiState.value.pression.first,
                        timestamp = monitorUiState.value.timestamp,
                        temperatureStatus = monitorUiState.value.temperature.second,
                        pressionStatus = monitorUiState.value.pression.second,
                        numWheels = monitorUiState.value.numWheels,
                        wheelsWithAlert = monitorUiState.value.wheelsWithAlert,
                        getSensorData = { sensorId -> monitorViewModel.getSensorDataByWheel(sensorId) },
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    val positionData = positionsUiState.value
                    when (positionData) {
                        is ResultApi.Success -> {
                            val data: List<DiagramMonitorResponse>? = positionData.data
                            PositionMonitorScreen(
                                sensorDataList = data,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        is ResultApi.Error -> {}
                        is ResultApi.Loading -> {
                            LoadingScreen()
                        }
                    }
                }
            }
        }
    }
}