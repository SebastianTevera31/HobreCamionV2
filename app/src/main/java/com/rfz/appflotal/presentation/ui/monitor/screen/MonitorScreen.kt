package com.rfz.appflotal.presentation.ui.monitor.screen

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.component.WarningSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.ListOfTireData
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorConfigurationUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.VOID_DATE

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
    onMountTireClick: (position: String) -> Unit,
    paymentPlan: PaymentPlanType,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    val monitorUiState by monitorViewModel.monitorUiState.collectAsState()
    val positionsUiState by monitorViewModel.positionsUiState.collectAsState()
    val monitorTireUiState by monitorViewModel.filteredTiresUiState.collectAsState()
    val tireUiState by monitorViewModel.tireUiState.collectAsState()
    val wifiStatus by monitorViewModel.wifiStatus.collectAsState()

    val context = LocalContext.current
    val buttonCancelText =
        if (paymentPlan == PaymentPlanType.Complete || monitorUiState.monitorId != 0) {
            stringResource(R.string.cerrar)
        } else stringResource(R.string.logout)

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
        navigateUp = navigateUp,
        showBackButton = showBackButton,
        onInspectClick = onInspectClick,
        onAssemblyClick = onAssemblyClick,
        onDisassemblyClick = onDisassemblyClick,
        onShowMonitorDialog = { show -> monitorViewModel.showMonitorDialog(show) },
        onGetLastedSensorData = { monitorViewModel.getLastedSensorData() },
        onUpdateSelectedTire = { tire -> monitorViewModel.updateSelectedTire(tire) },
        onGetSensorDataByWheel = { wheel -> monitorViewModel.getSensorDataByWheel(wheel) },
        onSwitchPressureUnit = { monitorViewModel.switchPressureUnit() },
        onSwitchTempUnit = { monitorViewModel.switchTemperatureUnit() },
        onGetTireDataByDate = { pos, date -> monitorViewModel.getTireDataByDate(pos, date) },
        onCleanFilteredTire = { monitorViewModel.cleanFilteredTire() },
        setupWizardSlot = if (paymentPlan == PaymentPlanType.Complete) {
            {
                MonitorSetupWizard(
                    monitorUiState = monitorUiState,
                    registerMonitorViewModel = registerMonitorViewModel,
                    paymentPlan = paymentPlan,
                    onMountTireClick = onMountTireClick,
                    onLinkedSensor = { monitorViewModel.initMonitorData() },
                    onFinish = { monitorViewModel.finishSetupWizard() },
                )
            }
        } else null,
        registerDialogSlot = {
            if (monitorUiState.showDialog &&
                (paymentPlan != PaymentPlanType.Complete || monitorUiState.monitorId != 0)
            ) {
                ShowMonitorRegisterDialog(
                    monitorId = monitorUiState.monitorId,
                    cancelButtonText = buttonCancelText,
                    registerMonitorViewModel = registerMonitorViewModel,
                    onDialogCancel = { onDialogCancel(monitorUiState.monitorId) },
                    onSuccessRegister = { _ ->
                        monitorViewModel.initMonitorData()
                    },
                    context = context,
                    paymentPlan = paymentPlan,
                )
            }
        },
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
    navigateUp: () -> Unit,
    showBackButton: Boolean = true,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onShowMonitorDialog: (Boolean) -> Unit,
    onGetLastedSensorData: () -> Unit,
    onUpdateSelectedTire: (String) -> Unit,
    onGetSensorDataByWheel: (String) -> Unit,
    onSwitchPressureUnit: () -> Unit,
    onSwitchTempUnit: () -> Unit,
    onGetTireDataByDate: (position: String, date: String) -> Unit,
    onCleanFilteredTire: () -> Unit,
    modifier: Modifier = Modifier,
    setupWizardSlot: (@Composable () -> Unit)? = null,
    registerDialogSlot: @Composable () -> Unit = {},
) {
    val context = LocalContext.current
    var selectedOption by rememberSaveable { mutableStateOf(MonitorScreenViews.DIAGRAMA) }

    if (!monitorUiState.showView) {
        LoadingView(modifier = modifier.fillMaxSize())
        return
    }

    when {
        setupWizardSlot != null && monitorUiState.showSetupWizard -> setupWizardSlot()
        else -> {
            registerDialogSlot()

            Scaffold(
                topBar = {
                    if (paymentPlan == PaymentPlanType.Complete) MonitorTopBar(
                        showDialog = {
                            if (wifiStatus == NetworkStatus.Connected) onShowMonitorDialog(true)
                            else Toast.makeText(
                                context,
                                R.string.error_conexion_internet,
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        showNavigationButton = showBackButton
                    ) { navigateUp() }
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
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = modifier
            ) { innerPadding ->
                Surface(modifier = Modifier.padding(innerPadding)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val isSignalUnknown =
                            monitorUiState.signalIntensity.first == BluetoothSignalQuality.Desconocida
                                    && monitorUiState.monitorId != 0
                                    && monitorUiState.monitorMac.isNotBlank()

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
                }
            }
        }
    }
}

@Composable
fun ShowMonitorRegisterDialog(
    monitorId: Int,
    cancelButtonText: String,
    registerMonitorViewModel: RegisterMonitorViewModel,
    onDialogCancel: () -> Unit,
    onSuccessRegister: (mac: Int) -> Unit,
    context: Context,
    paymentPlan: PaymentPlanType,
) {
    val configurationsUiState by registerMonitorViewModel.configurationList.collectAsState()
    val registerMonitorStatus by registerMonitorViewModel.registeredMonitorState.collectAsState()
    val monitorConfigUiState by registerMonitorViewModel.monitorConfigUiState.collectAsState()

    LaunchedEffect(Unit) {
        registerMonitorViewModel.loadConfigurationsIfEmpty()
    }

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

    ShowMonitorRegisterDialogContent(
        configurations = configurationsUiState,
        registerMonitorStatus = registerMonitorStatus,
        monitorConfigUiState = monitorConfigUiState,
        cancelButtonText = cancelButtonText,
        onDialogCancel = onDialogCancel,
        onScan = { registerMonitorViewModel.startScan() },
        onSuccessRegister = {
            onSuccessRegister(it)
            registerMonitorViewModel.clearMonitorRegistrationData()
        },
        onError = { registerMonitorViewModel.clearMonitorRegistrationData() },
        onMonitorConfiguration = { config ->
            registerMonitorViewModel.updateMonitorConfiguration(config)
        },
        onRegister = { mac, configuration ->
            registerMonitorViewModel.registerMonitor(
                idMonitor = monitorId,
                mac = mac,
                configurationSelected = configuration,
                context = context
            )
        },
        paymentPlan = paymentPlan
    )
}

@Composable
fun ShowMonitorRegisterDialogContent(
    configurations: Map<Int, String>,
    registerMonitorStatus: ApiResult<Int>,
    monitorConfigUiState: MonitorConfigurationUiState,
    cancelButtonText: String,
    onDialogCancel: () -> Unit,
    onScan: () -> Unit,
    onSuccessRegister: (mac: Int) -> Unit,
    onError: () -> Unit,
    onMonitorConfiguration: (Pair<Int, String>?) -> Unit,
    onRegister: (String, Pair<Int, String>?) -> Unit,
    paymentPlan: PaymentPlanType,
    modifier: Modifier = Modifier
) {
    MonitorRegisterDialog(
        macValue = monitorConfigUiState.mac,
        monitorSelected = monitorConfigUiState.configurationSelected,
        registerMonitorStatus = registerMonitorStatus,
        isScanning = monitorConfigUiState.isScanning,
        showCloseButton = true,
        onScan = onScan,
        configurations = configurations,
        onCloseButton = onDialogCancel,
        onSuccessRegister = onSuccessRegister,
        onError = onError,
        closeText = cancelButtonText,
        onMonitorConfiguration = onMonitorConfiguration,
        onContinueButton = onRegister,
        paymentPlan = paymentPlan,
        modifier = modifier
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonitorScreenPreview() {
    val sampleTires = listOf(
        MonitorTire("P1", inAlert = false, isAssembled = true, isActive = true, xPosition = 100, yPosition = 100),
        MonitorTire("P2", inAlert = true, isAssembled = true, isActive = true, xPosition = 200, yPosition = 100),
        MonitorTire("P3", inAlert = false, isAssembled = false, isActive = true, xPosition = 300, yPosition = 100),
        MonitorTire("P4", inAlert = false, isAssembled = true, isActive = false, xPosition = 400, yPosition = 100)
    )

    val monitorUiState = MonitorUiState(
        monitorId = 1,
        showView = true,
        listOfTires = sampleTires,
        temperatureUnit = UnidadTemperatura.CELCIUS,
        pressureUnit = UnidadPresion.PSI,
        signalIntensity = Pair(BluetoothSignalQuality.Excelente, "Excelente"),
        imageBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
        imageDimen = Pair(100, 100)
    )

    val tireUiState = TireUiState(
        currentTire = "P1",
        pressure = Pair(32.5f, SensorAlerts.NO_DATA),
        temperature = Pair(25.0f, SensorAlerts.NO_DATA),
        timestamp = VOID_DATE,
        isAssembled = true,
        isInspectionAvailable = true
    )

    val mockSensorData = listOf(
        ListOfTireData("P1", "T001", VOID_DATE, 32.5f, 25.0f),
        ListOfTireData("P2", "T002", VOID_DATE, 30.0f, 26.0f)
    )

    HombreCamionTheme {
        MonitorScreenContent(
            monitorUiState = monitorUiState,
            positionsUiState = ApiResult.Success(mockSensorData),
            monitorTireUiState = ApiResult.Success(emptyList()),
            tireUiState = tireUiState,
            wifiStatus = NetworkStatus.Connected,
            paymentPlan = PaymentPlanType.Complete,
            navigateUp = {},
            onInspectClick = { _, _, _ -> },
            onAssemblyClick = {},
            onDisassemblyClick = { _, _, _ -> },
            onShowMonitorDialog = {},
            onGetLastedSensorData = {},
            onUpdateSelectedTire = {},
            onGetSensorDataByWheel = {},
            onSwitchPressureUnit = {},
            onSwitchTempUnit = {},
            onGetTireDataByDate = { _, _ -> },
            onCleanFilteredTire = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShowMonitorRegisterDialogPreview() {
    val configurations = mapOf(1 to "TALON 1", 2 to "TALON 2")
    val monitorConfigUiState = MonitorConfigurationUiState(
        mac = "00:11:22:33:44:55",
        configurationSelected = Pair(1, "TALON 1"),
        isScanning = false
    )

    HombreCamionTheme {
        ShowMonitorRegisterDialogContent(
            configurations = configurations,
            registerMonitorStatus = ApiResult.Loading,
            monitorConfigUiState = monitorConfigUiState,
            cancelButtonText = "Cancelar",
            onDialogCancel = {},
            onScan = {},
            onSuccessRegister = {},
            onError = {},
            onMonitorConfiguration = {},
            onRegister = { _, _ -> },
            paymentPlan = PaymentPlanType.Complete
        )
    }
}
