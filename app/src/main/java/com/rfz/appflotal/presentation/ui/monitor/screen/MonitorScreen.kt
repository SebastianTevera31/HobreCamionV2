package com.rfz.appflotal.presentation.ui.monitor.screen

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel,
    registerMonitorViewModel: RegisterMonitorViewModel,
    navigateUp: () -> Unit,
    paymentPlan: PaymentPlanType,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val monitorUiState = monitorViewModel.monitorUiState.collectAsState()
    val positionsUiState = monitorViewModel.positionsUiState.collectAsState()
    val monitorTireUiState = monitorViewModel.monitorTireUiState.collectAsState()

    val configurationsUiState = registerMonitorViewModel.configurationList.collectAsState()
    val registerMonitorStatus = registerMonitorViewModel.registeredMonitorState.collectAsState()

    var selectedOption by rememberSaveable { mutableStateOf(MonitorScreenViews.DIAGRAMA) }

    // Carga la pantalla, vacia o no
    LaunchedEffect(Unit) {
        monitorViewModel.initMonitorData()
    }

    if (monitorUiState.value.showDialog) {
        // registerMonitorViewModel.loadConfigurations()
        LaunchedEffect(Unit) {
            registerMonitorViewModel.startScan()
        }

        registerMonitorViewModel.clearMonitorConfiguration()

        val monitorConfigUiState =
            registerMonitorViewModel.monitorConfigUiState.collectAsState()

        if (!monitorConfigUiState.value.isScanning) {
            registerMonitorViewModel.stopScan()
        }

        MonitorRegisterDialog(
            macValue = monitorConfigUiState.value.mac,
            monitorSelected = null,
            registerMonitorStatus = registerMonitorStatus.value,
            isScanning = monitorConfigUiState.value.isScanning,
            onScan = { registerMonitorViewModel.startScan() },
            configurations = configurationsUiState.value,
            onSuccessRegister = {
                monitorViewModel.initMonitorData()
                registerMonitorViewModel.clearMonitorRegistrationData()
            }
        ) { mac, configuration ->
            registerMonitorViewModel.registerMonitor(
                mac = mac,
                configurationSelected = configuration,
                context = context
            )
        }
    }

    Scaffold(
        topBar = { if (paymentPlan == PaymentPlanType.Complete) MonitorTopBar { navigateUp() } },
        bottomBar = {
            MonitorBottomNavBar(
                onClick = { view ->
                    if (view == MonitorScreenViews.POSICION) monitorViewModel.getListSensorData()
                    selectedOption = view
                },
                selectedView = selectedOption
            )
        },

    ) { contentPadding ->

        Surface(
            modifier = Modifier
        ) {
            Column(
                modifier = modifier
                    .background(Color("#EDF0F8".toColorInt())),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (selectedOption == MonitorScreenViews.DIAGRAMA) {
                    DiagramaMonitorScreen(
                        imageUrl = monitorUiState.value.chassisImageUrl,
                        currentWheel = monitorUiState.value.currentTire,
                        temperature = monitorUiState.value.temperature.first,
                        pressure = monitorUiState.value.pression.first,
                        timestamp = monitorUiState.value.timestamp,
                        temperatureStatus = monitorUiState.value.temperature.second,
                        pressionStatus = monitorUiState.value.pression.second,
                        numWheels = monitorUiState.value.numWheels,
                        alertTires = monitorUiState.value.tiresWithAlert,
                        updateSelectedTire = { selectedTire ->
                            monitorViewModel.updateSelectedTire(selectedTire)
                        },
                        getSensorData = { sensorId ->
                            monitorViewModel.getSensorDataByWheel(sensorId)
                        },
                        coordinates = monitorUiState.value.coordinateList,
                        imageDimens = monitorUiState.value.imageDimen,
                        modifier = Modifier.padding(8.dp),
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
                                is ApiResult.Success -> {
                                    val data: List<DiagramMonitorResponse>? = positionData.data
                                    CurrentPositionDataView(
                                        sensorDataList = monitorViewModel.convertToTireData(data),
                                        isOnSearch = false,
                                    )
                                }

                                is ApiResult.Error -> {}
                                is ApiResult.Loading -> {
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
                                is ApiResult.Success -> {
                                    val data: List<MonitorTireByDateResponse>? =
                                        monitorTireData.data?.sortedByDescending { it.sensorDate }
                                    CurrentPositionDataView(
                                        sensorDataList = data,
                                        isOnSearch = true
                                    )
                                }

                                is ApiResult.Error -> {}
                                is ApiResult.Loading -> {
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showSearchRecords) MaterialTheme.colorScheme.tertiary else Color(
                            "#2E3192".toColorInt()
                        )
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.recientes))
                }

                Button(
                    onClick = {
                        onPositionOptionSelected(R.string.filtrar)
                        showSearchRecords = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showSearchRecords) MaterialTheme.colorScheme.tertiary else Color(
                            "#2E3192".toColorInt()
                        )
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.filtrar))
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
