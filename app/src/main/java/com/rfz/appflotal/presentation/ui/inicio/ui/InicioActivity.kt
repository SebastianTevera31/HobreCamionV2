package com.rfz.appflotal.presentation.ui.inicio.ui

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rfz.appflotal.core.network.NetworkConfig
import com.rfz.appflotal.core.util.HombreCamionScreens
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.data.network.service.HombreCamionService
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
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.backgroundLight
import com.rfz.appflotal.presentation.ui.brand.MarcasScreen
import com.rfz.appflotal.presentation.ui.home.screen.HomeScreen
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.screen.InicioScreen
import com.rfz.appflotal.presentation.ui.inicio.viewmodel.InicioScreenViewModel
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.loading.screen.LoadingScreen
import com.rfz.appflotal.presentation.ui.login.screen.LoginScreen
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginViewModel
import com.rfz.appflotal.presentation.ui.medidasllantasscreen.MedidasLlantasScreen
import com.rfz.appflotal.presentation.ui.monitor.screen.MonitorScreen
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.montajedesmontajescreen.MontajeDesmontajeScreen
import com.rfz.appflotal.presentation.ui.nuevorenovadoscreen.NuevoRenovadoScreen
import com.rfz.appflotal.presentation.ui.nuevorenovadoscreen.RenovadosScreen
import com.rfz.appflotal.presentation.ui.originaldesign.OriginalScreen
import com.rfz.appflotal.presentation.ui.password.screen.PasswordScreen
import com.rfz.appflotal.presentation.ui.password.viewmodel.PasswordViewModel
import com.rfz.appflotal.presentation.ui.permission.PermissionScreen
import com.rfz.appflotal.presentation.ui.productoscreen.NuevoProductoScreen
import com.rfz.appflotal.presentation.ui.registrollantasscreen.NuevoRegistroLlantasScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screen.SignUpScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screen.TerminosScreen
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel
import com.rfz.appflotal.presentation.ui.registrovehiculosscreen.NuevoRegistroVehiculoScreen
import com.rfz.appflotal.presentation.ui.updateuserscreen.screen.UpdateUserScreen
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UpdateUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    @Inject
    lateinit var acquisitionTypeUseCase: AcquisitionTypeUseCase

    @Inject
    lateinit var providerListUseCase: ProviderListUseCase

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            var allGranted by remember { mutableStateOf(false) }
            val ctx = LocalContext.current

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
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = backgroundLight
                    ) {
                        val navController = rememberNavController()

                        NetworkConfig.imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        } else {
                            val tel =
                                getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                            tel.imei
                        }

                        val hasInitialValidation by inicioScreenViewModel.initialValidationCompleted.observeAsState(
                            false
                        )
                        val userData by inicioScreenViewModel.userData.observeAsState()

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
                                            ChronoUnit.HOURS.between(fechaUsuario, fechaActual)

                                        if (diferenciaHoras < 24) {

                                            if (!data.termsGranted) {
                                                navController.navigate(NavScreens.TERMINOS) {
                                                    popUpTo(NavScreens.LOADING) { inclusive = true }
                                                }
                                            } else {
                                                if (!arePermissionsGranted(
                                                        this@InicioActivity,
                                                        getRequiredPermissions()
                                                    )
                                                ) {
                                                    navController.navigate(NavScreens.PERMISOS) {
                                                        popUpTo(NavScreens.LOADING) { inclusive = true }
                                                    }
                                                } else {
                                                    navController.navigate(NavScreens.HOME) {
                                                        popUpTo(NavScreens.LOADING) {
                                                            inclusive = true
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            inicioScreenViewModel.deleteUserData()
                                            navController.navigate(NavScreens.LOGIN) {
                                                popUpTo(NavScreens.LOADING) { inclusive = true }
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


                        loginViewModel.navigateToHome.observe(this) { shouldNavigate ->
                            if (shouldNavigate.first) {
                                if (!shouldNavigate.third) {
                                    navController.navigate(NavScreens.TERMINOS) {
                                        popUpTo(NavScreens.LOADING) { inclusive = true }
                                    }
                                }else {
                                    if (!arePermissionsGranted(
                                            this@InicioActivity,
                                            getRequiredPermissions()
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
                                }

                                loginViewModel.onNavigateToHomeCompleted()
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = NavScreens.LOADING
                        ) {
                            composable(NavScreens.HOME) { backStackEntry ->

                                // Efecto: si ya están concedidos, arrancar servicio automáticamente
                                LaunchedEffect(Unit) {
                                    if (arePermissionsGranted(
                                            this@InicioActivity,
                                            getRequiredPermissions()
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
                                            this@InicioActivity,
                                            getRequiredPermissions()
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
                                    onDialogCancel = {
                                        CoroutineScope(Dispatchers.IO).launch {

                                            HombreCamionService.stopService(ctx)

                                            homeViewModel.logout()

                                            registerMonitorViewModel.clearMonitorRegistrationData()
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
                                    },
                                )
                            }

                            composable(NavScreens.RECUPERAR_CONTRASENIA) {
                                PasswordScreen(passwordViewModel)
                            }

                            composable(NavScreens.LOADING) { LoadingScreen() }
                            composable(NavScreens.LOGIN) {
                                LoginScreen(
                                    loginViewModel,
                                    homeViewModel,
                                    navController
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
                            composable(NavScreens.RENOVADOS) { RenovadosScreen(navController) }
                            composable(NavScreens.NUEVO_RENOVADO) {
                                NuevoRenovadoScreen(
                                    navController
                                )
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
                                    navController,
                                    acquisitionTypeUseCase,
                                    providerListUseCase,
                                    baseUseCase,
                                    productListUseCase,
                                    tireCrudUseCase,
                                    tireListUsecase,
                                    tireGetUseCase,
                                    homeViewModel
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
                                    navArgument("brandId") { type = NavType.IntType },
                                    navArgument("desc") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    }
                                )
                            ) { backStackEntry ->
                                val brandId = backStackEntry.arguments?.getInt("brandId") ?: 0
                                val description = backStackEntry.arguments?.getString("desc")
                            }

                            composable(route = NavScreens.REGISTRAR_USUARIO) {
                                val uiState = homeViewModel.uiState.collectAsState()
                                SignUpScreen(
                                    navController,
                                    languageSelected = uiState.value.selectedLanguage,
                                    signUpViewModel = signUpViewModel
                                ) { paymentPlanType ->
                                    if (!arePermissionsGranted(
                                            this@InicioActivity,
                                            getRequiredPermissions()
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
                                            popUpTo(NavScreens.PERMISOS) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(route = NavScreens.TERMINOS) {
                                TerminosScreen(
                                    this@InicioActivity
                                ) {
                                    loginViewModel.acceptTermsConditions()
                                    if (!arePermissionsGranted(
                                            this@InicioActivity,
                                            getRequiredPermissions()
                                        )
                                    ) {
                                        navController.navigate(NavScreens.PERMISOS) {
                                            popUpTo(NavScreens.LOADING) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(NavScreens.HOME) {
                                            popUpTo(NavScreens.LOADING) {
                                                inclusive = true
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