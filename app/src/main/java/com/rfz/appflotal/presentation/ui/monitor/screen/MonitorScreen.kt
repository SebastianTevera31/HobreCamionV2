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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.component.WarningSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.ListOfTireData
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState

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
    val wifiStatus by monitorViewModel.wifiStatus.collectAsState()

    LaunchedEffect(monitorUiState.monitorId) {
        monitorViewModel.initMonitorData()
    }

    MonitorScreenContent(
        monitorUiState = monitorUiState,
        positionsUiState = positionsUiState,
        monitorTireUiState = monitorTireUiState,
        tireUiState = tireUiState,
        wifiStatus = wifiStatus,
        paymentPlan = paymentPlan,
        onDialogCancel = onDialogCancel,
        navigateUp = navigateUp,
        onInspectClick = onInspectClick,
        onAssemblyClick = onAssemblyClick,
        onDisassemblyClick = onDisassemblyClick,
        onShowMonitorDialog = { show -> monitorViewModel.showMonitorDialog(show) },
        onGetLastedSensorData = { monitorViewModel.getLastedSensorData() },
        onGetBitmapImage = { monitorViewModel.getBitmapImage() },
        onUpdateSelectedTire = { tire -> monitorViewModel.updateSelectedTire(tire) },
        onGetSensorDataByWheel = { wheel -> monitorViewModel.getSensorDataByWheel(wheel) },
        onSwitchPressureUnit = { monitorViewModel.switchPressureUnit() },
        onSwitchTempUnit = { monitorViewModel.switchTemperatureUnit() },
        onGetTireDataByDate = { pos, date -> monitorViewModel.getTireDataByDate(pos, date) },
        onCleanFilteredTire = { monitorViewModel.cleanFilteredTire() },
        registerMonitorViewModel = registerMonitorViewModel,
        modifier = modifier
    )
}

@Composable

fun MonitorScreenContent(
    monitorUiState: MonitorUiState,
    positionsUiState: ApiResult<List<ListOfTireData>?>,
    monitorTireUiState: ApiResult<List<ListOfTireData>?>,
    tireUiState: TireUiState,
    wifiStatus: NetworkStatus,
    paymentPlan: PaymentPlanType,
    onDialogCancel: (mac: Int) -> Unit,
    navigateUp: () -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onShowMonitorDialog: (Boolean) -> Unit,
    onGetLastedSensorData: () -> Unit,
    onGetBitmapImage: () -> Unit,
    onUpdateSelectedTire: (String) -> Unit,
    onGetSensorDataByWheel: (String) -> Unit,
    onSwitchPressureUnit: () -> Unit,
    onSwitchTempUnit: () -> Unit,
    onGetTireDataByDate: (position: String, date: String) -> Unit,
    onCleanFilteredTire: () -> Unit,
    registerMonitorViewModel: RegisterMonitorViewModel?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedOption by rememberSaveable { mutableStateOf(MonitorScreenViews.DIAGRAMA) }

    val buttonCancelText =
        if (paymentPlan == PaymentPlanType.Complete || monitorUiState.monitorId != 0) {
            stringResource(R.string.cerrar)
        } else stringResource(R.string.logout)

    if (monitorUiState.showView && monitorUiState.showDialog && registerMonitorViewModel != null) {
        ShowMonitorRegisterDialog(
            monitorId = monitorUiState.monitorId,
            cancelButtonText = buttonCancelText,
            registerMonitorViewModel = registerMonitorViewModel,
            onDialogCancel = { onDialogCancel(monitorUiState.monitorId) },
            onSuccessRegister = { onGetBitmapImage() }, // This should ideally be passed down or handled better
            context = context,
        )
    }

    Scaffold(
        topBar = {
            if (paymentPlan == PaymentPlanType.Complete) MonitorTopBar(showDialog = {
                if (wifiStatus == NetworkStatus.Connected) onShowMonitorDialog(true)
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
                    if (view == MonitorScreenViews.POSICION) onGetLastedSensorData()
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
                    onGetBitmapImage()

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
                            updateSelectedTire = onUpdateSelectedTire,
                            getSensorData = onGetSensorDataByWheel,
                            tires = monitorUiState.listOfTires,
                            imageDimens = monitorUiState.imageDimen,
                            onInspectClick = onInspectClick,
                            onAssemblyClick = onAssemblyClick,
                            onDisassemblyClick = onDisassemblyClick,
                            onSwitchPressureUnit = onSwitchPressureUnit,
                            onSwitchTempUnit = onSwitchTempUnit,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        PositionScreenContentInternal(
                            paymentPlan = paymentPlan,
                            pressureUnit = monitorUiState.pressureUnit.symbol,
                            temperatureUnit = monitorUiState.temperatureUnit.symbol,
                            positionsUiState = positionsUiState,
                            monitorTireUiState = monitorTireUiState,
                            listOfTires = monitorUiState.listOfTires,
                            onGetTireDataByDate = onGetTireDataByDate,
                            onCleanFilteredTire = onCleanFilteredTire
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
    cancelButtonText: String,
    registerMonitorViewModel: RegisterMonitorViewModel,
    onDialogCancel: () -> Unit,
    onSuccessRegister: () -> Unit,
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
            onSuccessRegister()
            registerMonitorViewModel.clearMonitorRegistrationData()
        },
        onError = { registerMonitorViewModel.clearMonitorRegistrationData() },
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
    PositionScreenContentInternal(
        paymentPlan = paymentPlan,
        pressureUnit = pressureUnit,
        temperatureUnit = temperatureUnit,
        positionsUiState = positionsUiState,
        monitorTireUiState = monitorTireUiState,
        listOfTires = listOfTires,
        onGetTireDataByDate = { pos, date -> monitorViewModel.getTireDataByDate(pos, date) },
        onCleanFilteredTire = { monitorViewModel.cleanFilteredTire() }
    )
}

@Composable
private fun PositionScreenContentInternal(
    paymentPlan: PaymentPlanType,
    pressureUnit: String,
    temperatureUnit: String,
    positionsUiState: ApiResult<List<ListOfTireData>?>,
    monitorTireUiState: ApiResult<List<ListOfTireData>?>,
    listOfTires: List<MonitorTire>?,
    onGetTireDataByDate: (String, String) -> Unit,
    onCleanFilteredTire: () -> Unit
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
                onSensorData = onGetTireDataByDate,
                onPositionOptionSelected = { option -> positionOptionSelected = option }
            )

            if (positionOptionSelected == PositionView.RECIENTES) {
                RecentPositionsView(
                    positionsUiState = positionsUiState,
                    onClearFilteredTire = onCleanFilteredTire,
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
                onClearFilteredTire = onCleanFilteredTire,
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

@Preview(showBackground = true)
@Composable
fun MonitorScreenPreview() {
    HombreCamionTheme {
        MonitorScreenContent(
            monitorUiState = MonitorUiState(
                showView = true,
                monitorId = 1,
                listOfTires = listOf(
                    MonitorTire("P1", false, true, true, 100, 100),
                    MonitorTire("P2", true, true, true, 200, 100)
                ),
                temperatureUnit = UnidadTemperatura.CELCIUS,
                pressureUnit = UnidadPresion.PSI
            ),
            positionsUiState = ApiResult.Success(
                listOf(
                    ListOfTireData("P1", "123456", "2023-10-27 10:00:00", 100f, 35f)
                )
            ),
            monitorTireUiState = ApiResult.Success(emptyList()),
            tireUiState = TireUiState(
                currentTire = "P1",
                pressure = Pair(
                    100f,
                    com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts.NO_DATA
                ),
                temperature = Pair(
                    35f,
                    com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts.NO_DATA
                )
            ),
            wifiStatus = NetworkStatus.Connected,
            paymentPlan = PaymentPlanType.Complete,
            onDialogCancel = {},
            navigateUp = {},
            onInspectClick = { _, _, _ -> },
            onAssemblyClick = { _ -> },
            onDisassemblyClick = { _, _, _ -> },
            onShowMonitorDialog = {},
            onGetLastedSensorData = {},
            onGetBitmapImage = {},
            onUpdateSelectedTire = {},
            onGetSensorDataByWheel = {},
            onSwitchPressureUnit = {},
            onSwitchTempUnit = {},
            onGetTireDataByDate = { _, _ -> },
            onCleanFilteredTire = {},
            registerMonitorViewModel = null
        )
    }
}
