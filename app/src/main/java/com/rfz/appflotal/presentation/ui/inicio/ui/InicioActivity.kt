package com.rfz.appflotal.presentation.ui.inicio.ui

import android.Manifest
import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.rfz.appflotal.presentation.theme.ProyectoFscSoftTheme
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

import com.rfz.appflotal.presentation.ui.montajedesmontajescreen.MontajeDesmontajeScreen
import com.rfz.appflotal.presentation.ui.nuevorenovadoscreen.NuevoRenovadoScreen
import com.rfz.appflotal.presentation.ui.nuevorenovadoscreen.RenovadosScreen
import com.rfz.appflotal.presentation.ui.originaldesign.OriginalScreen
import com.rfz.appflotal.presentation.ui.productoscreen.NuevoProductoScreen

import com.rfz.appflotal.presentation.ui.registrollantasscreen.NuevoRegistroLlantasScreen

import com.rfz.appflotal.presentation.ui.registrovehiculosscreen.NuevoRegistroVehiculoScreen

import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class InicioActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val inicioScreenViewModel: InicioScreenViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private val monitorViewModel: MonitorViewModel by viewModels()

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
            val context = LocalContext.current

            val permisoBluetoothConnect = Manifest.permission.BLUETOOTH_CONNECT

            val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    // Si quieres, puedes inicializar tu servicio Bluetooth desde aquí
                    monitorViewModel.initService(context)
                } else {
                    Log.d("Permiso", "Permiso BLUETOOTH_CONNECT denegado")
                }
            }

            ProyectoFscSoftTheme {
                LocalizedApp {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()


                        NetworkConfig.imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        } else {
                            val tel =
                                getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            tel.imei
                        }

                        val hasInitialValidation by inicioScreenViewModel.initialValidationCompleted.observeAsState(
                            false
                        )
                        val userData by inicioScreenViewModel.userData.observeAsState()


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
                                            navController.navigate(NetworkConfig.HOME) {
                                                popUpTo(NetworkConfig.LOADING) { inclusive = true }
                                            }
                                        } else {
                                            inicioScreenViewModel.deleteUserData()
                                            navController.navigate(NetworkConfig.LOGIN) {
                                                popUpTo(NetworkConfig.LOADING) { inclusive = true }
                                            }
                                        }
                                    }
                                } ?: run {
                                    navController.navigate(NetworkConfig.LOGIN) {
                                        popUpTo(NetworkConfig.LOADING) { inclusive = true }
                                    }
                                }
                            }
                        }


                        loginViewModel.navigateToHome.observe(this) { shouldNavigate ->
                            if (shouldNavigate) {
                                navController.navigate(NetworkConfig.HOME) {
                                    popUpTo(NetworkConfig.LOGIN) { inclusive = true }
                                }
                                loginViewModel.onNavigateToHomeComplete()
                            }
                        }


                        NavHost(
                            navController = navController,
                            startDestination = NetworkConfig.LOADING
                        ) {
                            composable(NetworkConfig.LOADING) { LoadingScreen() }
                            composable(NetworkConfig.LOGIN) {
                                LoginScreen(
                                    loginViewModel,
                                    navController
                                )
                            }
                            composable(NetworkConfig.MARCAS) {
                                MarcasScreen(
                                    navController = navController,
                                    brandListUseCase = brandListUseCase,
                                    homeViewModel = homeViewModel,
                                    brandCrudUseCase = brandCrudUseCase
                                )
                            }
                            composable(NetworkConfig.ORIGINAL) {
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
                            composable(NetworkConfig.RENOVADOS) { RenovadosScreen(navController) }
                            composable(NetworkConfig.NUEVO_RENOVADO) {
                                NuevoRenovadoScreen(
                                    navController
                                )
                            }
                            composable(NetworkConfig.MEDIDAS_LLANTAS) {
                                MedidasLlantasScreen(
                                    navController,
                                    tireSizeUseCase,
                                    homeViewModel,
                                    tireSizeCrudUseCase
                                )
                            }
                            composable(NetworkConfig.PRODUCTOS) {
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
                            composable(NetworkConfig.NUEVO_PRODUCTO) {
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
                            composable(NetworkConfig.REGISTRO_LLANTAS) {
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
                            composable(NetworkConfig.REGISTRO_VEHICULOS) {
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
                            composable(NetworkConfig.MONTAJE_DESMONTAJE) {
                                MontajeDesmontajeScreen(
                                    navController
                                )
                            }
                            composable(NetworkConfig.INICIO) { InicioScreen(navController) }

                            composable(
                                route = "${NetworkConfig.NUEVA_MARCA}/{brandId}?desc={desc}",
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

                            composable(HombreCamionScreens.MONITOR.name) {
                                LaunchedEffect(Unit) {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context,
                                        permisoBluetoothConnect
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!granted) {
                                        bluetoothPermissionLauncher.launch(permisoBluetoothConnect)
                                    } else {
                                        monitorViewModel.initService(context)
                                    }
                                }

                                MonitorScreen(
                                    monitorViewModel = monitorViewModel
                                )
                            }

                            composable(NetworkConfig.HOME) {
                                HomeScreen(
                                    navController = navController,
                                    homeViewModel = homeViewModel,
                                    colors = MaterialTheme.colorScheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}