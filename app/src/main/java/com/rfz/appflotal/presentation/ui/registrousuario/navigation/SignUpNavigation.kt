package com.rfz.appflotal.presentation.ui.registrousuario.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.UnitProvider
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.components.UserInfoTopBar
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.preferences.PreferencesScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screen.LoginStatus
import com.rfz.appflotal.presentation.ui.registrousuario.screen.SignUpStatus
import com.rfz.appflotal.presentation.ui.registrousuario.screens.TermsDataScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screens.UserDataScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screens.VehicleDataScreen
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.AuthFlow
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel

enum class SignUpRoutes(val route: String) {
    USER_DATA_VIEW("user_data"),
    VEHICLE_DATA_VIEW("vehicle_data"),
    PREFERENCES("preferences_data"),
    TIRES("tire_data"),
    TERMS_VIEW("terms_view")
}

/**
 * Registra el flujo de registro en el NavGraph principal.
 * Ahora usa una única ruta "principal" que contiene un NavHost interno para evitar glitches.
 */
fun NavGraphBuilder.signUpGraph(
    navController: NavController,
    languageSelected: String,
    navigateToMenu: suspend (PaymentPlanType) -> Unit
) {
    composable(route = NavScreens.REGISTRAR_USUARIO) {
        SignUpFlowMainContainer(
            externalNavController = navController,
            languageSelected = languageSelected,
            navigateToMenu = navigateToMenu
        )
    }
}

@Composable
fun SignUpFlowMainContainer(
    externalNavController: NavController,
    languageSelected: String,
    navigateToMenu: suspend (PaymentPlanType) -> Unit
) {
    val ctx = LocalContext.current
    // El ViewModel se asocia a esta pantalla principal (el contenedor de todo el flujo)
    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState by viewModel.signUpUiState.collectAsState()
    val signUpRequestStatus = viewModel.signUpRequestStatus
    val loginRequestStatus = viewModel.loginRequestStatus

    var temperatureUnit: UnitProvider by remember { mutableStateOf(UnidadTemperatura.CELCIUS) }
    var odometerUnit: UnitProvider by remember { mutableStateOf(UnidadOdometro.MILLAS) }
    var pressureUnit: UnitProvider by remember { mutableStateOf(UnidadPresion.BAR) }

    // NavController interno para los pasos del registro
    val internalNavController = rememberNavController()
    val internalBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentInternalRoute = internalBackStackEntry?.destination?.route ?: "user_data"

    val snackbarHostState = remember { SnackbarHostState() }
    var authFlow by remember { mutableStateOf<AuthFlow>(AuthFlow.None) }
    val connectionError = stringResource(R.string.error_conexion_internet)

    LaunchedEffect(Unit) {
        if (uiState.countries.isEmpty()) {
            viewModel.populateListMenus(languageSelected)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Solo mostramos el TopBar si no estamos en términos
            if (currentInternalRoute != "terms_view") {
                UserInfoTopBar(
                    showNavigateUp = true, // Siempre mostramos atrás dentro del flujo
                    onNavigateUp = {
                        if (currentInternalRoute == "user_data") {
                            // Si estamos en el primer paso, salimos del registro al Login
                            viewModel.cleanSignUpData()
                            externalNavController.popBackStack()
                        } else {
                            // Si estamos en otro paso, volvemos al anterior internamente
                            internalNavController.popBackStack()
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Fondo decorativo (estático para todo el flujo)
                if (currentInternalRoute != "terms_view") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .drawWithContent {
                                val path = Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(size.width, 0f)
                                    lineTo(size.width, size.height * 0.1f)
                                    quadraticTo(
                                        size.width / 2,
                                        size.height * 0.2f,
                                        0f,
                                        size.height * 0.1f
                                    )
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(primaryLight, secondaryLight),
                                        startY = 0f,
                                        endY = size.height
                                    )
                                )
                            }
                    )
                }

                // NavHost Interno: Solo anima el contenido de los formularios
                NavHost(
                    navController = internalNavController,
                    startDestination = "user_data",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(SignUpRoutes.USER_DATA_VIEW.route) {
                        UserDataScreen(viewModel, internalNavController)
                    }
                    composable(SignUpRoutes.VEHICLE_DATA_VIEW.route) {
                        VehicleDataScreen(viewModel, internalNavController) {
                            internalNavController.navigate(SignUpRoutes.PREFERENCES.route)
                        }
                    }
                    composable(SignUpRoutes.TERMS_VIEW.route) {
                        TermsDataScreen(
                            viewModel,
                            internalNavController,
                            onRegisterStarted = { authFlow = AuthFlow.SignUp },
                            onShowConnectionError = {
                                snackbarHostState.showSnackbar(connectionError)
                            }
                        )
                    }
                    composable(SignUpRoutes.PREFERENCES.route) {
                        PreferencesScreen(
                            temperatureUnit = temperatureUnit,
                            pressureUnit = pressureUnit,
                            odometerUnit = odometerUnit,
                            onTempChange = {
                                temperatureUnit = it
                            },
                            onPressureChange = {
                                pressureUnit = it
                            },
                            onOdometerChange = {
                                odometerUnit = it
                            }
                        ) { _, _, _ ->
                            internalNavController.navigate(SignUpRoutes.TIRES.route)
                        }
                    }
                    composable(SignUpRoutes.TIRES.route) {

                    }
                }
            }
        }
    }

    // Lógica de diálogos y estados de red (Global para el flujo)
    if (authFlow == AuthFlow.SignUp || authFlow == AuthFlow.Login) {
        LoadingDialog()
    }

    when (authFlow) {
        AuthFlow.SignUp -> {
            SignUpStatus(
                ctx = ctx,
                onEnableButton = { },
                signUpRequestStatus = signUpRequestStatus,
                onFailure = { authFlow = AuthFlow.None }
            ) {
                authFlow = AuthFlow.Login
                viewModel.onLogin(ctx)
            }
        }

        AuthFlow.Login -> {
            LoginStatus(
                ctx = ctx,
                loginRequestStatus = loginRequestStatus,
                onFailure = { authFlow = AuthFlow.None }
            ) {
                navigateToMenu(uiState.paymentPlan)
                authFlow = AuthFlow.None
            }
        }

        else -> {}
    }
}
