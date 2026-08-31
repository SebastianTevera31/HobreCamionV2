package com.rfz.appflotal.presentation.ui.inicio.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.rfz.appflotal.BuildConfig
import com.rfz.appflotal.R
import com.rfz.appflotal.core.network.NetworkConfig
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.data.ConsentManager
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.data.repository.fcmessaging.AppNotificationState
import com.rfz.appflotal.data.repository.fcmessaging.AppStatusManagerRepository
import com.rfz.appflotal.data.repository.fcmessaging.MaintenanceStatus
import com.rfz.appflotal.domain.base.BaseUseCase
import com.rfz.appflotal.domain.brand.BrandCrudUseCase
import com.rfz.appflotal.domain.brand.BrandListUseCase
import com.rfz.appflotal.domain.controltype.ControlTypeUseCase
import com.rfz.appflotal.domain.originaldesign.CrudOriginalDesignUseCase
import com.rfz.appflotal.domain.originaldesign.OriginalDesignByIdUseCase
import com.rfz.appflotal.domain.originaldesign.OriginalDesignUseCase
import com.rfz.appflotal.domain.product.ProductByIdUseCase
import com.rfz.appflotal.domain.product.ProductCrudUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.route.RouteUseCase
import com.rfz.appflotal.domain.tire.LoadingCapacityUseCase
import com.rfz.appflotal.domain.tire.TireSizeCrudUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import com.rfz.appflotal.domain.utilization.UtilizationUseCase
import com.rfz.appflotal.domain.vehicle.VehicleByIdUseCase
import com.rfz.appflotal.domain.vehicle.VehicleCrudUseCase
import com.rfz.appflotal.domain.vehicle.VehicleListUseCase
import com.rfz.appflotal.domain.vehicle.VehicleTypeUseCase
import com.rfz.appflotal.presentation.commons.MaintenanceAppScreen
import com.rfz.appflotal.presentation.commons.UpdateAppScreen
import com.rfz.appflotal.presentation.navigation.authGraph
import com.rfz.appflotal.presentation.navigation.catalogGraph
import com.rfz.appflotal.presentation.navigation.mainNavigation
import com.rfz.appflotal.presentation.navigation.operationsNavigation
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.navigation.couponGraph
import com.rfz.appflotal.presentation.ui.forums.navigation.forumsGraph
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.components.ObserveOnResume
import com.rfz.appflotal.presentation.ui.inicio.viewmodel.InicioScreenViewModel
import com.rfz.appflotal.presentation.ui.inicio.viewmodel.NotificationPermissionState
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginViewModel
import com.rfz.appflotal.presentation.ui.monitor.component.WarningSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.reportes.navigation.reportGraph
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UpdateUserViewModel
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class InicioActivity : ComponentActivity() {
    private lateinit var consentManager: ConsentManager
    private val loginViewModel: LoginViewModel by viewModels()
    private val inicioScreenViewModel: InicioScreenViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val monitorViewModel: MonitorViewModel by viewModels()
    private val registerMonitorViewModel: RegisterMonitorViewModel by viewModels()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    @Inject
    lateinit var appStatusManagerRepository: AppStatusManagerRepository

    // Temporales para catalogGraph (idealmente mover a ViewModels después)
    @Inject lateinit var brandListUseCase: BrandListUseCase
    @Inject lateinit var brandCrudUseCase: BrandCrudUseCase
    @Inject lateinit var originalDesignUseCase: OriginalDesignUseCase
    @Inject lateinit var originalDesignByIdUseCase: OriginalDesignByIdUseCase
    @Inject lateinit var crudOriginalDesignUseCase: CrudOriginalDesignUseCase
    @Inject lateinit var utilizationUseCase: UtilizationUseCase
    @Inject lateinit var tireSizeUseCase: TireSizeUseCase
    @Inject lateinit var tireSizeCrudUseCase: TireSizeCrudUseCase
    @Inject lateinit var productListUseCase: ProductListUseCase
    @Inject lateinit var productCrudUseCase: ProductCrudUseCase
    @Inject lateinit var productByIdUseCase: ProductByIdUseCase
    @Inject lateinit var loadingCapacityUseCase: LoadingCapacityUseCase
    @Inject lateinit var vehicleListUseCase: VehicleListUseCase
    @Inject lateinit var vehicleCrudUseCase: VehicleCrudUseCase
    @Inject lateinit var vehicleByIdUseCase: VehicleByIdUseCase
    @Inject lateinit var vehicleTypeUseCase: VehicleTypeUseCase
    @Inject lateinit var controlTypeUseCase: ControlTypeUseCase
    @Inject lateinit var routeUseCase: RouteUseCase
    @Inject lateinit var baseUseCase: BaseUseCase

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this, getString(R.string.notifications_permission_granted), Toast.LENGTH_SHORT
            ).show()
        }
    }


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Create channel to show notifications.
        val channelId = getString(R.string.app_fcm_channel)
        val channelName = getString(R.string.app_fcm_flotal_channel)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        consentManager = ConsentManager(this)
        // INICIALIZAR ANUNCIO
        consentManager.requestConsent {
            MobileAds.initialize(this) {}
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            val prefs by lazy {
                getSharedPreferences("permissions_prefs", MODE_PRIVATE)
            }
            var allGranted by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val showBanner = when (backStackEntry?.destination?.route) {
                NavScreens.LOGIN, NavScreens.TERMINOS, NavScreens.INFORMACION_USUARIO, NavScreens.PERMISOS, NavScreens.REGISTRAR_USUARIO -> false
                else -> true
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

            val context = LocalContext.current

            val inicioState = appStatusManagerRepository.appState.collectAsState()

            val uiState = inicioScreenViewModel.uiState.collectAsState()
            val userData = uiState.value.userData
            val hasInitialValidation = uiState.value.initialValidationCompleted

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                val wasRequestedBefore = inicioScreenViewModel.permissionsRequested(prefs)

                val deniedPermissions = result.filterValues { granted -> !granted }.keys

                val permanentlyDenied = deniedPermissions.any { permission ->
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        this@InicioActivity, permission
                    )
                } && wasRequestedBefore

                inicioScreenViewModel.markPermissionsRequested(prefs)

                if (deniedPermissions.isEmpty()) {
                    allGranted = true
                    if (!isServiceRunning(this@InicioActivity, HombreCamionService::class.java)) {
                        HombreCamionService.startService(this@InicioActivity)
                    }
                } else {
                    allGranted = false

                    if (permanentlyDenied) {
                        Log.d("Permiso", "Denegado permanentemente")
                        inicioScreenViewModel.openAppSettings(context)
                    } else {
                        Log.d("Permiso", "Denegado temporalmente")
                    }
                }
            }

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->

                val state = if (granted) {
                    NotificationPermissionState.Granted
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this@InicioActivity, Manifest.permission.POST_NOTIFICATIONS
                        )
                    ) {
                        NotificationPermissionState.PermanentlyDenied
                    } else {
                        NotificationPermissionState.Denied
                    }
                }

                inicioScreenViewModel.updatePermissionState(state)
            }

            ObserveOnResume {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    inicioScreenViewModel.updatePermissionState(
                        NotificationPermissionState.Granted
                    )
                } else {
                    val granted = ContextCompat.checkSelfPermission(
                        this@InicioActivity, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        inicioScreenViewModel.updatePermissionState(
                            NotificationPermissionState.Granted
                        )
                    }

                }
            }

            HombreCamionTheme {
                LocalizedApp {
                    when (uiState.value.notificationPermission) {
                        NotificationPermissionState.NotRequested -> {
                            NotificationPermissionDialog(
                                onDismiss = { finish() },
                                onConfirmation = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }
                                })
                        }

                        NotificationPermissionState.Denied -> {
                            NotificationPermissionDialog(
                                onDismiss = { finish() },
                                onConfirmation = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }
                                })
                        }

                        NotificationPermissionState.PermanentlyDenied -> {
                            NotificationPermissionDialog(
                                onDismiss = { finish() },
                                onConfirmation = {
                                    inicioScreenViewModel.openAppSettings(this@InicioActivity)
                                })
                        }

                        NotificationPermissionState.Granted -> {
                            // Continúas flujo normal
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF213DF3), // Blue
                                        Color(0xFF4CAF50)  // Green
                                    )
                                )
                            ), color = MaterialTheme.colorScheme.primary
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (showBanner && inicioState.value.paymentPlanType == PaymentPlanType.Free) {
                                GlobalAdMobBanner(
                                    adUnitId = BuildConfig.AD_UNIT_ID,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                )
                            }

                            WarningSnackBanner(
                                visible = inicioState.value.isMaintenance == MaintenanceStatus.SCHEDULED,
                                message = stringResource(
                                    R.string.mensaje_mantenimiento_programado,
                                    inicioState.value.initialUpdateDataForUser
                                ),
                                containerColor = Color("#A6D4F2".toColorInt()),
                                contentColor = Color.Black,
                                paddingValues = PaddingValues(0.dp),
                            )

                            if (uiState.value.adView != null) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth()) {

                                    }
                                }
                            }

                            Box {
                                NetworkConfig.imei =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        Settings.Secure.getString(
                                            contentResolver, Settings.Secure.ANDROID_ID
                                        )
                                    } else {
                                        val tel =
                                            getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                                        tel.imei
                                    }

                                // Control de traslado de pantalla cuando se inicia la aplicacion
                                LaunchedEffect(hasInitialValidation, userData, lifecycleState) {
                                    if (hasInitialValidation && lifecycleState == Lifecycle.State.RESUMED) {
                                        val currentRoute = navController.currentDestination?.route

                                        userData?.let { data ->
                                            val fechaRegistro = data.fecha
                                            if (fechaRegistro.isNotEmpty()) {
                                                val formatter =
                                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                val fechaUsuario = LocalDateTime.parse(
                                                    fechaRegistro, formatter
                                                )
                                                val fechaActual = LocalDateTime.now()

                                                val diferenciaHoras = ChronoUnit.HOURS.between(
                                                    fechaUsuario, fechaActual
                                                )

                                                if (diferenciaHoras < 24) {

                                                    if (!data.termsGranted) {
                                                        if (currentRoute != NavScreens.TERMINOS) {
                                                            navController.navigate(NavScreens.TERMINOS) {
                                                                popUpTo(NavScreens.LOADING) {
                                                                    inclusive = true
                                                                }
                                                                launchSingleTop = true
                                                            }
                                                        }
                                                    } else {
                                                        delay(500.milliseconds)
                                                        val permissionsGranted =
                                                            arePermissionsGranted(
                                                                this@InicioActivity,
                                                                getRequiredPermissions()
                                                            )

                                                        if (!permissionsGranted) {
                                                            if (currentRoute != NavScreens.PERMISOS) {
                                                                navController.navigate(NavScreens.PERMISOS) {
                                                                    popUpTo(NavScreens.LOADING) {
                                                                        inclusive = true
                                                                    }
                                                                    launchSingleTop = true
                                                                }
                                                            }
                                                        } else {
                                                            if (currentRoute == NavScreens.LOADING) {
                                                                navController.navigate(NavScreens.HOME) {
                                                                    popUpTo(0) { inclusive = true }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    if (currentRoute != NavScreens.LOGIN) {
                                                        inicioScreenViewModel.deleteUserData()
                                                        navController.navigate(NavScreens.LOGIN) {
                                                            popUpTo(NavScreens.LOADING) {
                                                                inclusive = true
                                                            }
                                                            launchSingleTop = true
                                                        }
                                                    }
                                                }
                                            }
                                        } ?: run {
                                            if (currentRoute == NavScreens.LOADING) {
                                                navController.navigate(NavScreens.LOGIN) {
                                                    popUpTo(NavScreens.LOADING) { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    }
                                }

                                NavHost(
                                    navController = navController,
                                    startDestination = NavScreens.LOADING,
                                    enterTransition = {
                                        slideIntoContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(400)
                                        ) + fadeIn(animationSpec = tween(400))
                                    },
                                    exitTransition = {
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(400)
                                        ) + fadeOut(animationSpec = tween(400))
                                    },
                                    popEnterTransition = {
                                        slideIntoContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Right,
                                            animationSpec = tween(400)
                                        ) + fadeIn(animationSpec = tween(400))
                                    },
                                    popExitTransition = {
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Right,
                                            animationSpec = tween(400)
                                        ) + fadeOut(animationSpec = tween(400))
                                    }) {
                                    
                                    authGraph(
                                        navController = navController,
                                        loginViewModel = loginViewModel,
                                        inicioScreenViewModel = inicioScreenViewModel,
                                        homeViewModel = homeViewModel,
                                        allGranted = allGranted,
                                        permissionLauncher = permissionLauncher
                                    )

                                    mainNavigation(
                                        navController = navController,
                                        homeViewModel = homeViewModel,
                                        monitorViewModel = monitorViewModel,
                                        registerMonitorViewModel = registerMonitorViewModel,
                                        updateUserViewModel = updateUserViewModel
                                    )

                                    catalogGraph(
                                        navController = navController,
                                        homeViewModel = homeViewModel,
                                        brandListUseCase = brandListUseCase,
                                        brandCrudUseCase = brandCrudUseCase,
                                        originalDesignUseCase = originalDesignUseCase,
                                        originalDesignByIdUseCase = originalDesignByIdUseCase,
                                        crudOriginalDesignUseCase = crudOriginalDesignUseCase,
                                        utilizationUseCase = utilizationUseCase,
                                        tireSizeUseCase = tireSizeUseCase,
                                        tireSizeCrudUseCase = tireSizeCrudUseCase,
                                        productListUseCase = productListUseCase,
                                        productCrudUseCase = productCrudUseCase,
                                        productByIdUseCase = productByIdUseCase,
                                        loadingCapacityUseCase = loadingCapacityUseCase,
                                        vehicleListUseCase = vehicleListUseCase,
                                        vehicleCrudUseCase = vehicleCrudUseCase,
                                        vehicleByIdUseCase = vehicleByIdUseCase,
                                        vehicleTypeUseCase = vehicleTypeUseCase,
                                        controlTypeUseCase = controlTypeUseCase,
                                        routeUseCase = routeUseCase,
                                        baseUseCase = baseUseCase
                                    )

                                    operationsNavigation(
                                        navController = navController,
                                        homeViewModel = homeViewModel
                                    )

                                    forumsGraph(navController)
                                    couponGraph(navController)
                                    reportGraph(navController)
                                }

                                NotificationComponent(
                                    inicioUiState = inicioState.value,
                                    onPlanChange = {
                                        // Estar logueado garantiza existe un userId y navegacion al menu principal
                                        if (inicioState.value.userId != null) {
                                            navController.navigate(NavScreens.HOME) {
                                                launchSingleTop = true
                                            }
                                        }
                                    },
                                    onCleanState = { appStatusManagerRepository.cleanNotificationsState() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun NotificationComponent(
    inicioUiState: AppNotificationState,
    onPlanChange: () -> Unit,
    onCleanState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (inicioUiState.eventType) {
        FireCloudMessagingType.CAMBIO_DE_PLAN -> {
            onPlanChange() // Navigation
        }

        FireCloudMessagingType.ACTUALIZACION -> {
            UpdateAppScreen(
                modifier = modifier
                    .fillMaxSize()
                    .clickable {})
        }

        FireCloudMessagingType.ARREGLO_URGENTE, FireCloudMessagingType.MANTENIMIENTO -> {
            when (inicioUiState.isMaintenance) {
                MaintenanceStatus.MAINTENANCE -> {
                    MaintenanceAppScreen(
                        modifier = modifier.clickable {},
                        horaFinal = inicioUiState.finalUpdateDataForUser
                    )
                }

                MaintenanceStatus.NOT_MAINTENANCE -> {
                    onCleanState()
                }

                MaintenanceStatus.SCHEDULED -> {}
            }
        }

        else -> {}
    }
}

fun getRequiredPermissions(): Array<String> {
    val permissions = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        // Android 11 o menor
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    return permissions.toTypedArray()
}

fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { perm ->
        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    return manager.getRunningServices(Int.MAX_VALUE).any {
        it.service.className == serviceClass.name
    }
}