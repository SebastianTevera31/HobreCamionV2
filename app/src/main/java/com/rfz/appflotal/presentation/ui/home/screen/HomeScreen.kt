package com.rfz.appflotal.presentation.ui.home.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.theme.onPrimaryLight
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.registrousuario.screen.TerminosScreen
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    registerMonitorViewModel: RegisterMonitorViewModel,
    updateUserData: (String) -> Unit,
    onInspectClick: (tire: String, temp: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temp: Float, pressure: Float) -> Unit,
    monitorViewModel: MonitorViewModel,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val resources = context.resources

    val uiState by homeViewModel.uiState.collectAsState()
    val wifiStatus = monitorViewModel.wifiStatus.collectAsState()

    val onlyLanguagesAllowedText = stringResource(R.string.only_languages_allowed)
    val languages = listOf("es" to "ES", "en" to "EN")

    val userName = uiState.userData?.fld_name ?: stringResource(R.string.operator)

    val paymentPlan = uiState.paymentPlanType
    val plates = uiState.userData?.vehiclePlates ?: ""

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        homeViewModel.loadInitialData()
        registerMonitorViewModel.stopScan()
    }
    val monitorUiState by monitorViewModel.monitorUiState.collectAsState()
    val positionsUiState by monitorViewModel.positionsUiState.collectAsState()
    val monitorTireUiState by monitorViewModel.filteredTiresUiState.collectAsState()
    val tireUiState by monitorViewModel.tireUiState.collectAsState()


    // BLOQUEAR BOTON DE RETROCESO DEL DISPOSITIVO
    BackHandler { }

    LaunchedEffect(uiState.selectedLanguage) {
        if (uiState.selectedLanguage == "es" || uiState.selectedLanguage == "en") {
            val result = homeViewModel.changeLanguage(uiState.selectedLanguage)
            if (result.isSuccess && result.getOrNull()?.mensaje == "Lenguaje cambiado correctamente.") {
                val locale = Locale(uiState.selectedLanguage)
                Locale.setDefault(locale)
                val config = Configuration(configuration)
                config.setLocale(locale)
                context.createConfigurationContext(config)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        } else {
            Toast.makeText(
                context,
                onlyLanguagesAllowedText,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    when (uiState.screenLoadStatus) {
        OperationStatus.Error -> {
            ErrorView(modifier = Modifier.padding())
        }

        OperationStatus.Loading -> {
            AwaitDialog()
        }

        OperationStatus.Success -> {
            Scaffold(
                topBar = {
                    HomeTopBar(
                        uiState = uiState,
                        languages = languages,
                        onLanguageSelected = { code ->
                            scope.launch {
                                homeViewModel.changeLanguage(code)
                            }
                        },
                        onLogout = {
                            CoroutineScope(Dispatchers.IO).launch {
                                HombreCamionService.stopService(context)
                                homeViewModel.logout()
                                registerMonitorViewModel.clearMonitorConfiguration()
                                registerMonitorViewModel.stopScan()
                                monitorViewModel.cleanMonitorData()

                                withContext(Dispatchers.Main) {
                                    navController.navigate(NavScreens.LOGIN) {
                                        // Pop-up hasta la raíz del grafo de navegación (ID 0)
                                        // y elimina TODO lo que hay en el back stack.
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        onShare = {
                            homeViewModel.cleanOperationStatus()
                            navController.navigate(NavScreens.COMENTARIOS)
                        },
                        onProfile = {
                            updateUserData(uiState.selectedLanguage)
                            navController.navigate(NavScreens.INFORMACION_USUARIO)
                        },
                    )
                }
            ) { innerPadding ->
                Box {
                    HomeContent(
                        paymentPlan = paymentPlan,
                        wifiStatus = wifiStatus.value,
                        onShowMonitorDialog = {
                            monitorViewModel.showMonitorDialog(
                                true
                            )
                        },
                        onNavigate = { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        },
                        plates = plates,
                        userName = userName,
                        onInspectClick = onInspectClick,
                        onAssemblyClick = onAssemblyClick,
                        onDisassemblyClick = onDisassemblyClick,
                        onGetLastedSensorData = { monitorViewModel.getLastedSensorData() },
                        onGetBitmapImage = { monitorViewModel.getBitmapImage() },
                        onUpdateSelectedTire = { tire -> monitorViewModel.updateSelectedTire(tire) },
                        onGetSensorDataByWheel = { wheel ->
                            monitorViewModel.getSensorDataByWheel(
                                wheel
                            )
                        },
                        onSwitchPressureUnit = { monitorViewModel.switchPressureUnit() },
                        onSwitchTempUnit = { monitorViewModel.switchTemperatureUnit() },
                        onGetTireDataByDate = { pos, date ->
                            monitorViewModel.getTireDataByDate(
                                pos,
                                date
                            )
                        },
                        onCleanFilteredTire = { monitorViewModel.cleanFilteredTire() },
                        uiState = monitorUiState,
                        positionUiState = positionsUiState,
                        onBack = {
                            navController.navigateUp()
                        },
                        registerMonitorViewModel = registerMonitorViewModel,
                        monitorTireUiState = monitorTireUiState,
                        tireUiState = tireUiState,
                        onDialogCancel = {},

                        modifier = Modifier.padding(innerPadding)
                    )

                    if (uiState.showTermsAndConditions) {
                        TerminosScreen(
                            context = context,
                            buttonText = R.string.confirmar,
                            onBack = {},
                            modifier = Modifier.clickable {}
                        ) {
                            homeViewModel.acceptNewTermsAndConditions()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ElegantMenuCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    primaryColor: Color = primaryLight,
    secondaryColor: Color = secondaryLight,
    cardBackground: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val highlightColor = onPrimaryLight

    Card(
        modifier = modifier
            .height(180.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = primaryColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            cardBackground,
                            highlightColor.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    secondaryColor.copy(alpha = 0.2f),
                                    primaryColor.copy(alpha = 0.1f)
                                ),
                                center = Offset(0.3f, 0.3f),
                                radius = 100f
                            )
                        )
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        modifier = Modifier
                            .size(40.dp),
                        colorFilter = ColorFilter.tint(primaryColor)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}
