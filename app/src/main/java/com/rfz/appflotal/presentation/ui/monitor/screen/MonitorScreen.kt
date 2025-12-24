package com.rfz.appflotal.presentation.ui.monitor.screen

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.component.WarningSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.ListOfTireData
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel

enum class PositionView {
    RECIENTES, FILTRAR
}

@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel,
    onDialogCancel: (mac: Int) -> Unit,
    registerMonitorViewModel: RegisterMonitorViewModel,
    navigateUp: () -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    paymentPlan: PaymentPlanType,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val monitorUiState by monitorViewModel.monitorUiState.collectAsState()
    val positionsUiState by monitorViewModel.positionsUiState.collectAsState()
    val monitorTireUiState by monitorViewModel.filteredTiresUiState.collectAsState()
    val tireUiState by monitorViewModel.tireUiState.collectAsState()
    val wifiStatus = monitorViewModel.wifiStatus.collectAsState()

    var selectedOption by rememberSaveable { mutableStateOf(MonitorScreenViews.DIAGRAMA) }

    val buttonCancelText =
        if (paymentPlan == PaymentPlanType.Complete || monitorUiState.monitorId != 0) {
            stringResource(R.string.cerrar)
        } else stringResource(R.string.logout)

    LaunchedEffect(monitorUiState.monitorId) {
        monitorViewModel.initMonitorData()
    }

    if (monitorUiState.showView && monitorUiState.showDialog) {
        ShowMonitorRegisterDialog(
            monitorId = monitorUiState.monitorId,
            monitorViewModel = monitorViewModel,
            cancelButtonText = buttonCancelText,
            registerMonitorViewModel = registerMonitorViewModel,
            onDialogCancel = { onDialogCancel(monitorUiState.monitorId) },
            context = context,
        )
    }

    Scaffold(
        topBar = {
            if (paymentPlan == PaymentPlanType.Complete) MonitorTopBar(showDialog = {
                if (wifiStatus.value == NetworkStatus.Connected) monitorViewModel.showMonitorDialog(
                    true
                )
                else Toast.makeText(
                    context,
                    R.string.error_conexion_internet,
                    Toast.LENGTH_LONG
                ).show()
            }) { navigateUp() }
        },
        bottomBar = {
            MonitorBottomNavBar(
                onClick = { view ->
                    if (view == MonitorScreenViews.POSICION) monitorViewModel.getLastedSensorData()
                    selectedOption = view
                },
                selectedView = selectedOption
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (monitorUiState.showView) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val isSignalUnknown =
                        monitorUiState.signalIntensity.first == BluetoothSignalQuality.Desconocida
                                && monitorUiState.monitorId != 0
                    monitorViewModel.getBitmapImage()

                    if (isSignalUnknown) {
                        val text = stringResource(
                            monitorUiState.signalIntensity.first.alertMessage!!
                        )

                        WarningSnackBanner(
                            visible = true,
                            message = text
                        )
                    }
                    if (selectedOption == MonitorScreenViews.DIAGRAMA) {
                        DiagramaMonitorScreen(
                            paymentPlan = paymentPlan,
                            tireUiState = tireUiState,
                            temperatureUnit = monitorUiState.temperatureUnit.symbol,
                            pressureUnit = monitorUiState.pressureUnit.symbol,
                            image = monitorUiState.imageBitmap,
                            updateSelectedTire = { selectedTire ->
                                monitorViewModel.updateSelectedTire(selectedTire)
                            },
                            getSensorData = { sensorId ->
                                monitorViewModel.getSensorDataByWheel(sensorId)
                            },
                            tires = monitorUiState.listOfTires,
                            imageDimens = monitorUiState.imageDimen,
                            onInspectClick = { tire, temp, press ->
                                onInspectClick(
                                    tire,
                                    temp,
                                    press
                                )
                            },
                            onAssemblyClick = { tire ->
                                onAssemblyClick(tire)
                            },
                            onDisassemblyClick = { tire, temperature, pressure ->
                                onDisassemblyClick(tire, temperature, pressure)
                            },
                            onSwitchPressureUnit = {
                                monitorViewModel.switchPressureUnit()
                            },
                            onSwitchTempUnit = {
                                monitorViewModel.switchTemperatureUnit()
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        PositionScreenContent(
                            paymentPlan = paymentPlan,
                            monitorViewModel = monitorViewModel,
                            positionsUiState = positionsUiState,
                            monitorTireUiState = monitorTireUiState,
                            listOfTires = monitorUiState.listOfTires,
                            pressureUnit = monitorUiState.pressureUnit.symbol,
                            temperatureUnit = monitorUiState.temperatureUnit.symbol
                        )
                    }
                }
            } else {
                LoadingView()
            }
        }
    }
}

@Composable
private fun ShowMonitorRegisterDialog(
    monitorId: Int,
    monitorViewModel: MonitorViewModel,
    cancelButtonText: String,
    registerMonitorViewModel: RegisterMonitorViewModel,
    onDialogCancel: () -> Unit,
    context: Context
) {
    val configurationsUiState by registerMonitorViewModel.configurationList.collectAsState()
    val registerMonitorStatus by registerMonitorViewModel.registeredMonitorState.collectAsState()
    val monitorConfigUiState by registerMonitorViewModel.monitorConfigUiState.collectAsState()

    LaunchedEffect(monitorId) {
        if (monitorId == 0) {
            registerMonitorViewModel.clearMonitorRegistrationData()
            registerMonitorViewModel.clearMonitorConfiguration()
            registerMonitorViewModel.startScan()
        } else {
            registerMonitorViewModel.getMonitorConfiguration()
        }
    }

    if (!monitorConfigUiState.isScanning) {
        registerMonitorViewModel.stopScan()
    }

    MonitorRegisterDialog(
        macValue = monitorConfigUiState.mac,
        monitorSelected = monitorConfigUiState.configurationSelected,
        registerMonitorStatus = registerMonitorStatus,
        isScanning = monitorConfigUiState.isScanning,
        showCloseButton = true,
        onScan = { registerMonitorViewModel.startScan() },
        configurations = configurationsUiState,
        onCloseButton = onDialogCancel,
        onSuccessRegister = {
            monitorViewModel.initMonitorData()
            registerMonitorViewModel.clearMonitorRegistrationData()
        },
        closeText = cancelButtonText,
        onMonitorConfiguration = { config ->
            registerMonitorViewModel.updateMonitorConfiguration(config)
        }
    ) { mac, configuration ->
        registerMonitorViewModel.registerMonitor(
            mac = mac,
            configurationSelected = configuration,
            context = context
        )
    }
}

@Composable
private fun PositionScreenContent(
    paymentPlan: PaymentPlanType,
    monitorViewModel: MonitorViewModel,
    pressureUnit: String,
    temperatureUnit: String,
    positionsUiState: ApiResult<List<ListOfTireData>?>,
    monitorTireUiState: ApiResult<List<ListOfTireData>?>,
    listOfTires: List<MonitorTire>?
) {
    if (paymentPlan == PaymentPlanType.Complete) {
        var positionOptionSelected by remember { mutableStateOf(PositionView.RECIENTES) }
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavPositionMonitorScreen(
                selectedView = positionOptionSelected,
                tiresList = listOfTires,
                onSensorData = { sensorSelected, dateSelected ->
                    monitorViewModel.getTireDataByDate(sensorSelected, dateSelected)
                },
                onPositionOptionSelected = { option -> positionOptionSelected = option }
            )

            if (positionOptionSelected == PositionView.RECIENTES) {
                RecentPositionsView(
                    positionsUiState = positionsUiState,
                    onClearFilteredTire = { monitorViewModel.cleanFilteredTire() },
                    pressureUnit = pressureUnit,
                    temperatureUnit = temperatureUnit
                )
            } else {
                FilteredPositionsView(
                    monitorTireUiState = monitorTireUiState,
                    pressureUnit = pressureUnit,
                    temperatureUnit = temperatureUnit
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecentPositionsView(
                positionsUiState = positionsUiState,
                onClearFilteredTire = { monitorViewModel.cleanFilteredTire() },
                pressureUnit = pressureUnit,
                temperatureUnit = temperatureUnit
            )
        }
    }
}

@Composable
private fun RecentPositionsView(
    positionsUiState: ApiResult<List<ListOfTireData>?>,
    onClearFilteredTire: () -> Unit,
    pressureUnit: String,
    temperatureUnit: String
) {
    onClearFilteredTire()
    when (positionsUiState) {
        is ApiResult.Error -> NoPositionDataView(R.string.no_registros)
        ApiResult.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ApiResult.Success<List<ListOfTireData>?> -> {
            val data = positionsUiState.data
            CurrentPositionDataView(
                message = R.string.no_ruedas_activas,
                sensorDataList = data,
                isOnSearch = false,
                pressureUnit = pressureUnit,
                temperatureUnit = temperatureUnit,
            )
        }
    }
}

@Composable
private fun FilteredPositionsView(
    monitorTireUiState: ApiResult<List<ListOfTireData>?>,
    pressureUnit: String,
    temperatureUnit: String
) {
    when (monitorTireUiState) {
        is ApiResult.Success -> {
            val data: List<ListOfTireData>? =
                monitorTireUiState.data?.sortedByDescending { it.sensorDate }
            CurrentPositionDataView(
                message = R.string.no_registros,
                sensorDataList = data,
                isOnSearch = true,
                pressureUnit = pressureUnit,
                temperatureUnit = temperatureUnit,
            )
        }

        is ApiResult.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp),
            ) {
                NoPositionDataView(R.string.error_carga_datos)
            }
        }

        is ApiResult.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun NavPositionMonitorScreen(
    selectedView: PositionView,
    tiresList: List<MonitorTire>?,
    onSensorData: (String, String) -> Unit,
    onPositionOptionSelected: (PositionView) -> Unit,
    modifier: Modifier = Modifier
) {
    val showSearchRecords = selectedView == PositionView.FILTRAR

    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 16.dp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
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
                    onClick = { onPositionOptionSelected(PositionView.RECIENTES) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showSearchRecords) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(text = stringResource(R.string.recientes)) }

                Button(
                    onClick = { onPositionOptionSelected(PositionView.FILTRAR) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showSearchRecords) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(text = stringResource(R.string.filtrar)) }
            }

            if (showSearchRecords && tiresList?.any { it.isActive } == true) {
                PositionFilterView(
                    tiresList = tiresList,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) { wheelSelected, dateSelected ->
                    onSensorData(wheelSelected, dateSelected)
                }
            }
        }
    }
}
