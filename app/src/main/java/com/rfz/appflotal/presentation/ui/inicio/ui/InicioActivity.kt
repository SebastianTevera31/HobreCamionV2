package com.rfz.appflotal.presentation.ui.inicio.ui

import android.Manifest
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rfz.appflotal.R
import com.rfz.appflotal.core.network.NetworkConfig
import com.rfz.appflotal.core.util.AppVersionUtil
import com.rfz.appflotal.core.util.HombreCamionScreens
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.data.model.fcmessaging.AppUpdateMessage
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.data.repository.AppStatusManagerRepository
import com.rfz.appflotal.data.repository.MainUiState
import com.rfz.appflotal.data.repository.MaintenanceStatus
import com.rfz.appflotal.domain.acquisitiontype.AcquisitionTypeUseCase
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
import com.rfz.appflotal.domain.provider.ProviderListUseCase
import com.rfz.appflotal.domain.route.RouteUseCase
import com.rfz.appflotal.domain.tire.LoadingCapacityUseCase
import com.rfz.appflotal.domain.tire.TireCrudUseCase
import com.rfz.appflotal.domain.tire.TireGetUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.domain.tire.TireSizeCrudUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import com.rfz.appflotal.domain.utilization.UtilizationUseCase
import com.rfz.appflotal.domain.vehicle.VehicleByIdUseCase
import com.rfz.appflotal.domain.vehicle.VehicleCrudUseCase
import com.rfz.appflotal.domain.vehicle.VehicleListUseCase
import com.rfz.appflotal.domain.vehicle.VehicleTypeUseCase
import com.rfz.appflotal.presentation.commons.MaintenanceAppScreen
import com.rfz.appflotal.presentation.commons.UpdateAppScreen
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.backgroundLight
import com.rfz.appflotal.presentation.ui.assembly.screen.AssemblyTireScreen
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel
import com.rfz.appflotal.presentation.ui.brand.screen.MarcasScreen
import com.rfz.appflotal.presentation.ui.cambiodestino.screen.CambioDestinoScreen
import com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel.CambioDestinoViewModel
import com.rfz.appflotal.presentation.ui.dissassembly.screen.DisassemblyTireScreen
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyViewModel
import com.rfz.appflotal.presentation.ui.home.screen.HomeScreen
import com.rfz.appflotal.presentation.ui.home.screen.ShareFeedbackScreen
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.screen.InicioScreen
import com.rfz.appflotal.presentation.ui.inicio.viewmodel.InicioScreenViewModel
import com.rfz.appflotal.presentation.ui.inspection.screens.InspectionRoute
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionViewModel
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.loading.screen.LoadingScreen
import com.rfz.appflotal.presentation.ui.login.screen.LoginScreen
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginViewModel
import com.rfz.appflotal.presentation.ui.login.viewmodel.NavigationEvent
import com.rfz.appflotal.presentation.ui.marcarenovados.screens.MarcaRenovadosScreen
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.MarcaRenovadosViewModel
import com.rfz.appflotal.presentation.ui.medidasllantasscreen.MedidasLlantasScreen
import com.rfz.appflotal.presentation.ui.monitor.component.AdvertisementSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.screen.MonitorScreen
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.montajedesmontajescreen.MontajeDesmontajeScreen
import com.rfz.appflotal.presentation.ui.originaldesign.OriginalScreen
import com.rfz.appflotal.presentation.ui.password.screen.PasswordScreen
import com.rfz.appflotal.presentation.ui.password.viewmodel.PasswordViewModel
import com.rfz.appflotal.presentation.ui.permission.PermissionScreen
import com.rfz.appflotal.presentation.ui.productoscreen.NuevoProductoScreen
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.NuevoRegistroLlantasScreen
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.NuevoRegistroLlantasViewModel
import com.rfz.appflotal.presentation.ui.registrousuario.screen.SignUpScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screen.TerminosScreen
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel
import com.rfz.appflotal.presentation.ui.registrovehiculosscreen.NuevoRegistroVehiculoScreen
import com.rfz.appflotal.presentation.ui.repararrenovar.screen.RepararRenovarScreen
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.RepararRenovarViewModel
import com.rfz.appflotal.presentation.ui.retreatedesign.screens.RetreatedDesignScreen
import com.rfz.appflotal.presentation.ui.retreatedesign.viewmodel.RetreatedDesignViewModel
import com.rfz.appflotal.presentation.ui.scrap.screens.TireWastePileScreen
import com.rfz.appflotal.presentation.ui.scrap.viewmodel.TireWasteViewModel
import com.rfz.appflotal.presentation.ui.updateuserscreen.screen.UpdateUserScreen
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UpdateUserViewModel
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class InicioActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val inicioScreenViewModel: InicioScreenViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val passwordViewModel: PasswordViewModel by viewModels()
    private val monitorViewModel: MonitorViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val registerMonitorViewModel: RegisterMonitorViewModel by viewModels()
    private val updateUserViewModel: UpdateUserViewModel by viewModels()
    private val retreatedDesignViewModel: RetreatedDesignViewModel by viewModels()
    private val inspectionViewModel: InspectionViewModel by viewModels()
    private val nuevoRegistroLllantasViewModel: NuevoRegistroLlantasViewModel by viewModels()
    private val marcaRenovadosScreen: MarcaRenovadosViewModel by viewModels()

    private val assemblyTireViewModel: AssemblyTireViewModel by viewModels()

    private val disassemblyTireViewModel: DisassemblyViewModel by viewModels()

    private val tireWasteViewModel: TireWasteViewModel by viewModels()

    private val repararRenovarViewModel: RepararRenovarViewModel by viewModels()

    private val cambioDestinoViewModel: CambioDestinoViewModel by viewModels()


    @Inject
    lateinit var acquisitionTypeUseCase: AcquisitionTypeUseCase

    @Inject
    lateinit var providerListUseCase: ProviderListUseCase

    @Inject
    lateinit var appStatusManagerrRepository: AppStatusManagerRepository

    @Inject
    lateinit var tireCrudUseCase: TireCrudUseCase

    @Inject
    lateinit var tireListUsecase: TireListUsecase

    @Inject
    lateinit var tireGetUseCase: TireGetUseCase

    @Inject
    lateinit var brandCrudUseCase: BrandCrudUseCase

    @Inject
    lateinit var originalDesignByIdUseCase: OriginalDesignByIdUseCase

    @Inject
    lateinit var utilizationUseCase: UtilizationUseCase


    @Inject
    lateinit var productByIdUseCase: ProductByIdUseCase


    @Inject
    lateinit var brandListUseCase: BrandListUseCase

    @Inject
    lateinit var crudOriginalDesignUseCase: CrudOriginalDesignUseCase


    @Inject
    lateinit var tireSizeCrudUseCase: TireSizeCrudUseCase

    @Inject
    lateinit var productListUseCase: ProductListUseCase

    @Inject
    lateinit var productCrudUseCase: ProductCrudUseCase


    @Inject
    lateinit var originalDesignUseCase: OriginalDesignUseCase

    @Inject
    lateinit var tireSizeUseCase: TireSizeUseCase

    @Inject
    lateinit var loadingCapacityUseCase: LoadingCapacityUseCase

    @Inject
    lateinit var vehicleListUseCase: VehicleListUseCase

    @Inject
    lateinit var vehicleCrudUseCase: VehicleCrudUseCase

    @Inject
    lateinit var vehicleByIdUseCase: VehicleByIdUseCase

    @Inject
    lateinit var vehicleTypeUseCase: VehicleTypeUseCase

    @Inject
    lateinit var controlTypeUseCase: ControlTypeUseCase

    @Inject
    lateinit var routeUseCase: RouteUseCase

    @Inject
    lateinit var baseUseCase: BaseUseCase

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            var allGranted by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            val context = LocalContext.current

            val inicioState = appStatusManagerrRepository.mainUiState.collectAsState()
            val appVersionData = appStatusManagerrRepository.updateMessage.collectAsState()

            val postNotificationGranted =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else false

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                val todosConcedidos = result.values.all { it }
                if (todosConcedidos) {
                    allGranted = true
                    if (!isServiceRunning(this@InicioActivity, HombreCamionService::class.java)) {
                        HombreCamionService.startService(this@InicioActivity)
                    }
                } else {
                    allGranted = false
                    Log.d("Permiso", "❌ Permiso denegado")
                }
            }

            HombreCamionTheme {
                LocalizedApp {
                    if (!postNotificationGranted) {
                        askNotificationPermission()
                    }
                    Surface(
                        modifier = Modifier.fillMaxWidth(), color = backgroundLight
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AdvertisementSnackBanner(
                                visible = inicioState.value.isMaintenance == MaintenanceStatus.SCHEDULED,
                                message = stringResource(
                                    R.string.mensaje_mantenimiento_programado,
                                    inicioState.value.initialUpdateDataForUser
                                ),
                                containerColor = Color("#A6D4F2".toColorInt()),
                                contentColor = Color.Black,
                                paddingValues = PaddingValues(0.dp)
                            )

                            Box {
                                NetworkConfig.imei =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        Settings.Secure.getString(
                                            contentResolver,
                                            Settings.Secure.ANDROID_ID
                                        )
                                    } else {
                                        val tel =
                                            getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                                        tel.imei
                                    }

                                val hasInitialValidation by inicioScreenViewModel.initialValidationCompleted.observeAsState(
                                    false
                                )
                                val userData by inicioScreenViewModel.userData.observeAsState()

                                LaunchedEffect(Unit) {
                                    loginViewModel.navigationEvent.collect { event ->
                                        when (event) {
                                            NavigationEvent.NavigateToHome -> {
                                                navController.navigate(NavScreens.HOME) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }

                                            NavigationEvent.NavigateToPermissions -> {
                                                navController.navigate(NavScreens.PERMISOS) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Control de traslado de pantalla cuando se inicia la aplicacion
                                LaunchedEffect(hasInitialValidation, userData) {
                                    if (hasInitialValidation) {
                                        userData?.let { data ->
                                            val fechaRegistro = data.fecha
                                            if (fechaRegistro.isNotEmpty()) {
                                                val formatter =
                                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                val fechaUsuario =
                                                    LocalDateTime.parse(fechaRegistro, formatter)
                                                val fechaActual = LocalDateTime.now()

                                                val diferenciaHoras =
                                                    ChronoUnit.HOURS.between(
                                                        fechaUsuario,
                                                        fechaActual
                                                    )

                                                if (diferenciaHoras < 24) {

                                                    if (!data.termsGranted) {
                                                        navController.navigate(NavScreens.TERMINOS) {
                                                            popUpTo(NavScreens.LOADING) {
                                                                inclusive = true
                                                            }
                                                        }
                                                    } else {
                                                        if (!arePermissionsGranted(
                                                                this@InicioActivity,
                                                                getRequiredPermissions()
                                                            )
                                                        ) {
                                                            navController.navigate(NavScreens.PERMISOS) {
                                                                popUpTo(NavScreens.LOADING) {
                                                                    inclusive = true
                                                                }
                                                            }
                                                        } else {
                                                            navController.navigate(NavScreens.HOME) {
                                                                popUpTo(0) {
                                                                    inclusive = true
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    inicioScreenViewModel.deleteUserData()
                                                    navController.navigate(NavScreens.LOGIN) {
                                                        popUpTo(NavScreens.LOADING) {
                                                            inclusive = true
                                                        }
                                                    }
                                                }
                                            }
                                        } ?: run {
                                            navController.navigate(NavScreens.LOGIN) {
                                                popUpTo(NavScreens.LOADING) { inclusive = true }
                                            }
                                        }
                                    }
                                }

                                loginViewModel.navigateToHome.observe(this@InicioActivity) { shouldNavigate ->
                                    if (shouldNavigate.first) {
                                        if (!arePermissionsGranted(
                                                this@InicioActivity, getRequiredPermissions()
                                            )
                                        ) {
                                            navController.navigate(NavScreens.PERMISOS) {
                                                popUpTo(NavScreens.LOGIN) { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate(NavScreens.HOME) {
                                                popUpTo(NavScreens.LOGIN) { inclusive = true }
                                            }
                                        }

                                        loginViewModel.onNavigateToHomeCompleted()
                                    }
                                }

                                NavHost(
                                    navController = navController,
                                    startDestination = NavScreens.LOADING,
                                    enterTransition = {
                                        slideIntoContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(700)
                                        ) + fadeIn(animationSpec = tween(700))
                                    },
                                    exitTransition = {
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(700)
                                        ) + fadeOut(animationSpec = tween(700))
                                    },
                                    popEnterTransition = {
                                        slideIntoContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Right,
                                            animationSpec = tween(700)
                                        ) + fadeIn(animationSpec = tween(700))
                                    },
                                    popExitTransition = {
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Right,
                                            animationSpec = tween(700)
                                        ) + fadeOut(animationSpec = tween(700))
                                    }
                                ) {
                                    composable(NavScreens.HOME) {
                                        // Efecto: si ya están concedidos, arrancar servicio automáticamente
                                        LaunchedEffect(Unit) {
                                            if (arePermissionsGranted(
                                                    this@InicioActivity, getRequiredPermissions()
                                                )
                                            ) {
                                                if (!isServiceRunning(
                                                        this@InicioActivity,
                                                        HombreCamionService::class.java
                                                    )
                                                ) {
                                                    HombreCamionService.startService(this@InicioActivity)
                                                }
                                            }
                                        }

                                        HomeScreen(
                                            navController = navController,
                                            homeViewModel = homeViewModel,
                                            registerMonitorViewModel = registerMonitorViewModel,
                                            onInspectClick = { tire, temp, pressure ->
                                                val route =
                                                    "${NavScreens.INSPECCION}/$tire?temp=$temp&pressure=$pressure"
                                                navController.navigate(route) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            onAssemblyClick = { tire ->
                                                navController.navigate("${NavScreens.MONTAJE}/$tire") {
                                                    launchSingleTop = true
                                                }
                                            },
                                            onDisassemblyClick = { tire, temp, pressure ->
                                                val route =
                                                    "${NavScreens.DESMONTAJE}/$tire?temp=$temp&pressure=$pressure"
                                                navController.navigate(route) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            updateUserData = { selectedLanguage ->
                                                updateUserViewModel.fetchUserData(
                                                    selectedLanguage
                                                )
                                            },
                                            monitorViewModel = monitorViewModel
                                        )
                                    }

                                    composable(HombreCamionScreens.MONITOR.name) {
                                        // Efecto: si ya están concedidos, arrancar servicio automáticamente
                                        LaunchedEffect(Unit) {
                                            if (arePermissionsGranted(
                                                    this@InicioActivity, getRequiredPermissions()
                                                )
                                            ) {
                                                if (!isServiceRunning(
                                                        this@InicioActivity,
                                                        HombreCamionService::class.java
                                                    )
                                                ) {
                                                    HombreCamionService.startService(this@InicioActivity)
                                                }
                                            }
                                        }

                                        MonitorScreen(
                                            monitorViewModel = monitorViewModel,
                                            registerMonitorViewModel = registerMonitorViewModel,
                                            navigateUp = { navController.navigateUp() },
                                            paymentPlan = PaymentPlanType.Complete,
                                            onDialogCancel = { monitorId ->
                                                registerMonitorViewModel.stopScan()
                                                if (monitorId != 0) {
                                                    monitorViewModel.showMonitorDialog(false)
                                                } else {
                                                    navController.navigateUp()
                                                }
                                            },
                                            modifier = Modifier.fillMaxSize(),
                                            onInspectClick = { tire, temp, pressure ->
                                                val route =
                                                    "${NavScreens.INSPECCION}/$tire?temp=$temp&pressure=$pressure"
                                                navController.navigate(route) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            onAssemblyClick = { tire ->
                                                navController.navigate("${NavScreens.MONTAJE}/$tire") {
                                                    launchSingleTop = true
                                                }
                                            },
                                            onDisassemblyClick = { tire, temp, pressure ->
                                                val route =
                                                    "${NavScreens.DESMONTAJE}/$tire?temp=$temp&pressure=$pressure"
                                                navController.navigate(route) {
                                                    launchSingleTop = true
                                                }
                                            })
                                    }

                                    composable(NavScreens.RECUPERAR_CONTRASENIA) {
                                        PasswordScreen(passwordViewModel)
                                    }

                                    composable(NavScreens.LOADING) { LoadingScreen() }

                                    composable(NavScreens.LOGIN) {
                                        LoginScreen(
                                            loginViewModel, homeViewModel, navController
                                        )
                                    }

                                    composable(NavScreens.MARCAS) {
                                        MarcasScreen(
                                            navController = navController,
                                            brandListUseCase = brandListUseCase,
                                            homeViewModel = homeViewModel,
                                            brandCrudUseCase = brandCrudUseCase
                                        )
                                    }

                                    composable(NavScreens.ORIGINAL) {
                                        OriginalScreen(
                                            navController,
                                            originalDesignUseCase = originalDesignUseCase,
                                            originalDesignByIdUseCase,
                                            crudOriginalDesignUseCase,
                                            brandListUseCase,
                                            utilizationUseCase,
                                            homeViewModel
                                        )
                                    }

                                    composable(NavScreens.RENOVADOS) {
                                        RetreatedDesignScreen(
                                            viewModel = retreatedDesignViewModel,
                                            onBackScreen = { navController.popBackStack() })
                                    }

                                    composable(NavScreens.MARCA_RENOVADA) {
                                        MarcaRenovadosScreen(
                                            viewModel = marcaRenovadosScreen,
                                            onBackScreen = { navController.popBackStack() })
                                    }

                                    composable(NavScreens.MEDIDAS_LLANTAS) {
                                        MedidasLlantasScreen(
                                            navController,
                                            tireSizeUseCase,
                                            homeViewModel,
                                            tireSizeCrudUseCase
                                        )
                                    }

                                    composable(NavScreens.PRODUCTOS) {
                                        NuevoProductoScreen(
                                            navController,
                                            productListUseCase,
                                            productCrudUseCase,
                                            productByIdUseCase,
                                            originalDesignUseCase,
                                            tireSizeUseCase,
                                            loadingCapacityUseCase,
                                            homeViewModel
                                        )
                                    }

                                    composable(NavScreens.NUEVO_PRODUCTO) {
                                        NuevoProductoScreen(
                                            navController,
                                            productListUseCase,
                                            productCrudUseCase,
                                            productByIdUseCase,
                                            originalDesignUseCase,
                                            tireSizeUseCase,
                                            loadingCapacityUseCase,
                                            homeViewModel
                                        )
                                    }

                                    composable(NavScreens.REGISTRO_LLANTAS) {
                                        NuevoRegistroLlantasScreen(
                                            navController = navController,
                                            viewModel = nuevoRegistroLllantasViewModel
                                        )
                                    }

                                    composable(NavScreens.REGISTRO_VEHICULOS) {
                                        NuevoRegistroVehiculoScreen(
                                            navController,
                                            vehicleListUseCase,
                                            vehicleCrudUseCase,
                                            vehicleByIdUseCase,
                                            vehicleTypeUseCase,
                                            controlTypeUseCase,
                                            routeUseCase,
                                            baseUseCase,
                                            homeViewModel
                                        )
                                    }
                                    composable(NavScreens.MONTAJE_DESMONTAJE) {
                                        MontajeDesmontajeScreen(
                                            navController
                                        )
                                    }
                                    composable(NavScreens.INICIO) { InicioScreen(navController) }

                                    composable(
                                        route = "${NavScreens.NUEVA_MARCA}/{brandId}?desc={desc}",
                                        arguments = listOf(
                                            navArgument("brandId") {
                                                type = NavType.IntType
                                            },
                                            navArgument("desc") {
                                                type = NavType.StringType
                                                nullable = true
                                                defaultValue = null
                                            })
                                    ) { backStackEntry ->
                                        val brandId =
                                            backStackEntry.arguments?.getInt("brandId") ?: 0
                                        val description =
                                            backStackEntry.arguments?.getString("desc")
                                    }

                                    composable(route = NavScreens.REGISTRAR_USUARIO) {
                                        val uiState = homeViewModel.uiState.collectAsState()
                                        SignUpScreen(
                                            navController,
                                            languageSelected = uiState.value.selectedLanguage,
                                            signUpViewModel = signUpViewModel
                                        ) {
                                            if (!arePermissionsGranted(
                                                    this@InicioActivity, getRequiredPermissions()
                                                )
                                            ) {
                                                navController.navigate(NavScreens.PERMISOS) {
                                                    popUpTo(NavScreens.REGISTRAR_USUARIO) {
                                                        inclusive = true
                                                    }
                                                }
                                            } else {
                                                navController.navigate(NavScreens.HOME) {
                                                    popUpTo(NavScreens.REGISTRAR_USUARIO) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    composable(route = NavScreens.INFORMACION_USUARIO) {
                                        UpdateUserScreen(
                                            updateUserViewModel = updateUserViewModel,
                                        ) {
                                            navController.popBackStack()
                                        }
                                    }

                                    composable(route = NavScreens.PERMISOS) {
                                        PermissionScreen(
                                            context = this@InicioActivity,
                                            allGranted = allGranted,
                                            launcher = permissionLauncher,
                                            onGranted = {
                                                navController.navigate(NavScreens.HOME) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            })
                                    }

                                    composable(route = NavScreens.TERMINOS) {
                                        TerminosScreen(
                                            context = this@InicioActivity,
                                            buttonText = R.string.confirmar,
                                            onBack = {
                                                inicioScreenViewModel.deleteUserData()
                                                navController.popBackStack()
                                            }) {
                                            loginViewModel.acceptTermsConditions {
                                                !arePermissionsGranted(
                                                    this@InicioActivity, getRequiredPermissions()
                                                )
                                            }
                                        }
                                    }

                                    composable(
                                        route = "${NavScreens.INSPECCION}/{tire}?temp={temp}&pressure={pressure}",
                                        arguments = listOf(
                                            navArgument("tire") {
                                                type = NavType.StringType
                                            },
                                            navArgument("temp") {
                                                type = NavType.FloatType; defaultValue = 0
                                            },
                                            navArgument("pressure") {
                                                type = NavType.FloatType; defaultValue = 0
                                            })
                                    ) { backStackEntry ->
                                        val tire = backStackEntry.arguments?.getString("tire") ?: ""
                                        val temp = backStackEntry.arguments?.getFloat("temp") ?: 0.0
                                        val pressure =
                                            backStackEntry.arguments?.getFloat("pressure") ?: 0.0

                                        InspectionRoute(
                                            tire = tire,
                                            temperature = temp.toFloat(),
                                            pressure = pressure.toFloat(),
                                            onBack = {
                                                navController.navigate(HombreCamionScreens.MONITOR.name) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(HombreCamionScreens.MONITOR.name)
                                                }
                                            },
                                            onFinish = { tire ->
                                                navController.navigate(HombreCamionScreens.MONITOR.name) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(HombreCamionScreens.MONITOR.name)
                                                }
                                                monitorViewModel.getSensorDataByWheel(tire)
                                            },
                                            viewModel = inspectionViewModel
                                        )
                                    }

                                    composable(
                                        route = "${NavScreens.MONTAJE}/{tire}", arguments = listOf(
                                            navArgument("tire") {
                                                type = NavType.StringType
                                            })
                                    ) { backStackEntry ->
                                        val positionTire =
                                            backStackEntry.arguments?.getString("tire") ?: ""
                                        AssemblyTireScreen(
                                            positionTire = positionTire,
                                            viewModel = assemblyTireViewModel,
                                            onBack = {
                                                navController.popBackStack()
                                            })
                                    }

                                    composable(
                                        route = "${NavScreens.DESMONTAJE}/{tire}?temp={temp}&pressure={pressure}",
                                        arguments = listOf(
                                            navArgument("tire") {
                                                type = NavType.StringType
                                            },
                                            navArgument("temp") {
                                                type = NavType.FloatType; defaultValue = 0
                                            },
                                            navArgument("pressure") {
                                                type = NavType.FloatType; defaultValue = 0
                                            })
                                    ) { backStackEntry ->
                                        val tire = backStackEntry.arguments?.getString("tire") ?: ""
                                        val temp = backStackEntry.arguments?.getFloat("temp") ?: 0.0
                                        val pressure =
                                            backStackEntry.arguments?.getFloat("pressure") ?: 0.0
                                        DisassemblyTireScreen(
                                            positionTire = tire,
                                            initialTemperature = temp.toFloat(),
                                            initialPressure = pressure.toFloat(),
                                            viewModel = disassemblyTireViewModel,
                                            onBack = {
                                                navController.popBackStack()
                                            },
                                            onFinish = {
                                                navController.navigate(HombreCamionScreens.MONITOR.name) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(HombreCamionScreens.MONITOR.name)
                                                }
                                            })
                                    }

                                    composable(route = NavScreens.DESECHO) {
                                        TireWastePileScreen(
                                            onBack = { navController.popBackStack() },
                                            viewModel = tireWasteViewModel,
                                        )
                                    }

                                    composable(route = NavScreens.REPARARRENOVAR) {
                                        RepararRenovarScreen(
                                            onBack = { navController.popBackStack() },
                                            viewModel = repararRenovarViewModel,
                                        )
                                    }

                                    composable(route = NavScreens.CAMBIO_DESTINO) {
                                        CambioDestinoScreen(
                                            onBack = { navController.popBackStack() },
                                            viewModel = cambioDestinoViewModel,
                                        )
                                    }


                                    composable(route = NavScreens.COMENTARIOS) {
                                        val msgOperationState =
                                            homeViewModel.messageOperationState.collectAsState()
                                        ShareFeedbackScreen(
                                            onShare = { feedback ->
                                                homeViewModel.onSendFeedback(
                                                    feedback
                                                )
                                            },
                                            onBack = { navController.popBackStack() },
                                            messageOperationState = msgOperationState.value,
                                        )
                                    }
                                }

                                NotificationComponent(
                                    inicioUiState = inicioState.value,
                                    notificationData = appVersionData.value,
                                    onCleanState = { inicioScreenViewModel.cleanNotificationsState() },
                                    context = this@InicioActivity
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun NotificationComponent(
    inicioUiState: MainUiState,
    notificationData: AppUpdateMessage?,
    onCleanState: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context
) {
    notificationData?.let { msg ->
        when (msg.tipo) {
            FireCloudMessagingType.ACTUALIZACION.value -> {
                if (50 < AppVersionUtil.getVersionCode(context)) {
                    UpdateAppScreen(modifier = modifier.fillMaxSize())
                } else onCleanState()
            }

            FireCloudMessagingType.SERVICIO_AUTO.value -> {}
            FireCloudMessagingType.MANTENIMIENTO.value -> {
                when (inicioUiState.isMaintenance) {
                    MaintenanceStatus.MAINTENANCE -> {
                        MaintenanceAppScreen(
                            modifier = modifier,
                            horaFinal = inicioUiState.finalUpdateDataForUser
                        )
                    }

                    MaintenanceStatus.NOT_MAINTENANCE -> {
                        onCleanState()
                    }

                    MaintenanceStatus.SCHEDULED -> {

                    }
                }
            }

            FireCloudMessagingType.ARREGLO_URGENTE.value -> {}
        }
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