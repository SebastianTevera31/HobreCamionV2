package com.rfz.appflotal.presentation.ui.home.screen

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.presentation.theme.onPrimaryLight
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.home.utils.cardBackground
import com.rfz.appflotal.presentation.ui.home.utils.menuItems
import com.rfz.appflotal.presentation.ui.home.utils.primaryColor
import com.rfz.appflotal.presentation.ui.home.utils.secondaryColor
import com.rfz.appflotal.presentation.ui.home.utils.surfaceColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.screen.MonitorScreen
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    registerMonitorViewModel: RegisterMonitorViewModel,
    updateUserData: (String) -> Unit,
    monitorViewModel: MonitorViewModel,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val resources = context.resources

    val uiState by homeViewModel.uiState.collectAsState()
    val wifiStatus = monitorViewModel.wifiStatus.collectAsState()

    var showMonitorDialog by remember { mutableStateOf(false) }
    val registerMonitorStatus = registerMonitorViewModel.registeredMonitorState.collectAsState()

    val onlyLanguagesAllowedText = stringResource(R.string.only_languages_allowed)
    val languages = listOf("es" to "ES", "en" to "EN")

    val userName = uiState.userData?.fld_name ?: stringResource(R.string.operator)
    val paymentPlan =
        PaymentPlanType.valueOf(uiState.userData?.paymentPlan?.replace(" ", "") ?: "None")
    val plates = uiState.userData?.vehiclePlates ?: ""

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        homeViewModel.loadInitialData()
        registerMonitorViewModel.stopScan()
    }

    LaunchedEffect(showMonitorDialog) {
        registerMonitorViewModel.getMonitorConfiguration()
    }

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

    if (showMonitorDialog) {
        val configurations = registerMonitorViewModel.configurationList.collectAsState()
        val monitorConfigUiState = registerMonitorViewModel.monitorConfigUiState.collectAsState()
        val monitorUiState = monitorViewModel.monitorUiState.collectAsState()

        RegisterMonitorDialog(
            configurations = configurations.value,
            monitorConfigurationUiState = monitorConfigUiState.value,
            registerMonitorStatus = registerMonitorStatus.value,
            onCloseButton = {
                registerMonitorViewModel.stopScan()
                showMonitorDialog = false
            },
            onContinueButton = { mac, configuration ->
                registerMonitorViewModel.registerMonitor(
                    idMonitor = monitorUiState.value.monitorId,
                    mac = mac,
                    configurationSelected = configuration,
                    context = context
                )
            },
            onScan = { registerMonitorViewModel.startScan() },
            onMonitorConfiguration = { config ->
                registerMonitorViewModel.updateMonitorConfiguration(
                    config
                )
            }
        ) {
            showMonitorDialog = false
            monitorViewModel.initMonitorData()
            registerMonitorViewModel.clearMonitorRegistrationData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .height(120.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = stringResource(R.string.logo_description),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .padding(8.dp)
                                .height(54.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            languages.forEach { (code, display) ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            scope.launch {
                                                homeViewModel.changeLanguage(code)
                                            }
                                        }
                                        .background(
                                            if (uiState.selectedLanguage == code)
                                                Color.White.copy(alpha = 0.3f)
                                            else
                                                Color.Transparent
                                        )
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = display,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = if (uiState.selectedLanguage == code) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                if (code != languages.last().first) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {

                                HombreCamionService.stopService(context)

                                homeViewModel.logout()

                                registerMonitorViewModel.clearMonitorConfiguration()

                                registerMonitorViewModel.stopScan()

                                withContext(Dispatchers.Main) {
                                    // navController.clearBackStack(NavScreens.LOGIN)
                                    navController.navigate(NavScreens.LOGIN) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            monitorViewModel.clearMonitorData()
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.logout),
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            updateUserData(uiState.selectedLanguage)
                            navController.navigate(NavScreens.INFORMACION_USUARIO)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.profile),
                            tint = Color.White
                        )
                    }
                }
            )
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryLight),
                            startY = 0f,
                            endY = 500f
                        )
                    )
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(
                            bottomStart = 48.dp,
                            bottomEnd = 48.dp
                        ),
                        spotColor = primaryColor.copy(alpha = 0.3f)
                    )
            ) {
                UserHeader(
                    paymentPlan = paymentPlan,
                    userName = userName,
                    plates = plates
                ) {
                    if (wifiStatus.value == NetworkStatus.Connected) showMonitorDialog = true
                    else Toast.makeText(
                        context,
                        R.string.error_conexion_internet,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            // PANTALLAS
            if (paymentPlan != PaymentPlanType.Complete) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(surfaceColor)
                        .clip(
                            RoundedCornerShape(
                                topStart = 32.dp,
                                topEnd = 32.dp
                            )
                        )
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(menuItems) { item ->
                            ElegantMenuCard(
                                title = stringResource(item.title),
                                iconRes = item.iconRes,
                                onClick = { navController.navigate(item.route) },
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                cardBackground = cardBackground
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.9f),
                                    secondaryColor.copy(alpha = 0.9f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.copyright, LocalDate.now().year),
                        color = Color.White.copy(alpha = 0.95f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                MonitorScreen(
                    monitorViewModel = monitorViewModel,
                    registerMonitorViewModel = registerMonitorViewModel,
                    navigateUp = { navController.navigateUp() },
                    paymentPlan = paymentPlan,
                    modifier = Modifier.fillMaxSize(),
                    onDialogCancel = {
                        CoroutineScope(Dispatchers.IO).launch {

                            HombreCamionService.stopService(context)

                            homeViewModel.logout()

                            registerMonitorViewModel.clearMonitorConfiguration()

                            registerMonitorViewModel.stopScan()

                            withContext(Dispatchers.Main) {
                                // navController.clearBackStack(NavScreens.LOGIN)
                                navController.navigate(NavScreens.LOGIN) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        monitorViewModel.clearMonitorData()
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ElegantMenuCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    primaryColor: Color = primaryLight,
    secondaryColor: Color = secondaryLight,
    cardBackground: Color = Color.White
) {
    val highlightColor = onPrimaryLight

    Card(
        modifier = Modifier
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