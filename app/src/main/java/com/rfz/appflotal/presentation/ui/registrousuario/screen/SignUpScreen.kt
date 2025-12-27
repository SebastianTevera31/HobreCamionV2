package com.rfz.appflotal.presentation.ui.registrousuario.screen

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.login.response.Result
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.UserInfoTopBar
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.AuthFlow
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpAlerts
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel

enum class SignUpViews() {
    USER_DATA_VIEW, VEHICLE_DATA_VIEW, TERMS_VIEW
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    languageSelected: String,
    signUpViewModel: SignUpViewModel,
    modifier: Modifier = Modifier,
    navigateToMenu: (PaymentPlanType) -> Unit
) {
    val ctx = LocalContext.current
    var isNextScreen by remember { mutableStateOf(false) }

    val signUpUiState = signUpViewModel.signUpUiState.collectAsState()
    val signUpRequestStatus = signUpViewModel.signUpRequestStatus
    val loginRequestStatus = signUpViewModel.loginRequestStatus

    val snackbarHostState = remember { SnackbarHostState() }
    var enableRegisterButton by remember { mutableStateOf(true) }
    var authFlow by remember { mutableStateOf<AuthFlow>(AuthFlow.None) }

    val connectionError = stringResource(R.string.error_conexion_internet)

    LaunchedEffect(Unit) {
        signUpViewModel.populateListMenus(languageSelected)
        signUpViewModel.changeScreen(SignUpViews.USER_DATA_VIEW)
        signUpViewModel.cleanSignUpData()
    }

    Scaffold(topBar = {
        if (signUpUiState.value.currentScreen != SignUpViews.TERMS_VIEW) {
            UserInfoTopBar(
                showNavigateUp = !isNextScreen,
                onNavigateUp = {
                    signUpViewModel.cleanSignUpData()
                    navController.navigateUp()
                }
            )
        }
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .drawWithContent {
                        drawContent()
                        if (signUpUiState.value.currentScreen != SignUpViews.TERMS_VIEW) {
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
                    },
                contentAlignment = Alignment.Center
            ) {
                when (signUpUiState.value.currentScreen) {
                    SignUpViews.USER_DATA_VIEW -> {
                        UserForm(
                            title = R.string.registro,
                            profileData = signUpUiState.value.profileData,
                            modifier = Modifier
                                .padding(top = 80.dp)
                                .padding(horizontal = 40.dp),
                            countries = signUpUiState.value.countries,
                            sectors = signUpUiState.value.sectors
                        ) { name, password, email, country, sector ->
                            val message = signUpViewModel.chargeUserData(
                                name = name,
                                username = email,
                                email = email,
                                password = password,
                                country = country,
                                sector = sector
                            )
                            if (message == SignUpAlerts.DATAREGISTER_SUCCESSFULY) {
                                isNextScreen = true
                                signUpViewModel.changeScreen(SignUpViews.VEHICLE_DATA_VIEW)
                            } else Toast.makeText(
                                ctx,
                                message.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    SignUpViews.VEHICLE_DATA_VIEW -> {
                        VehicleForm(
                            title = R.string.registrar_vehiculo,
                            vehicleData = signUpUiState.value.vehicleData,
                            modifier = Modifier.padding(horizontal = 40.dp),
                            enableRegisterButton = enableRegisterButton,
                            onBack = { vehicleType, plates ->
                                signUpViewModel.chargeVehicleData(
                                    typeVehicle = vehicleType,
                                    plates = plates
                                )
                                signUpViewModel.changeScreen(SignUpViews.USER_DATA_VIEW)
                                isNextScreen = false
                            }
                        ) { vehicleType, plates ->
                            val message = signUpViewModel.chargeVehicleData(
                                typeVehicle = vehicleType,
                                plates = plates
                            )

                            if (message == SignUpAlerts.DATAREGISTER_SUCCESSFULY) {
                                signUpViewModel.changeScreen(SignUpViews.TERMS_VIEW)
                            } else {
                                Toast.makeText(
                                    ctx,
                                    message.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    SignUpViews.TERMS_VIEW -> {
                        TerminosScreen(
                            context = ctx,
                            onBack = {
                                navController.popBackStack()
                            }
                        ) {
                            authFlow = AuthFlow.SignUp
                            signUpViewModel.signUpUser(ctx) {
                                snackbarHostState.showSnackbar(
                                    message = connectionError,
                                    actionLabel = "OK"
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    if (authFlow == AuthFlow.SignUp || authFlow == AuthFlow.Login) {
        AwaitDialog()
    }

    when (authFlow) {
        AuthFlow.SignUp -> {
            SignUpStatus(
                ctx = ctx,
                onEnableButton = { enableRegisterButton = true },
                signUpRequestStatus = signUpRequestStatus,
                onFailure = {
                    authFlow = AuthFlow.None
                    signUpViewModel.changeScreen(SignUpViews.VEHICLE_DATA_VIEW)
                }
            ) {
                authFlow = AuthFlow.Login
                signUpViewModel.onLogin(ctx)
            }
        }

        AuthFlow.Login -> {
            LoginStatus(
                ctx = ctx,
                loginRequestStatus = loginRequestStatus,
                onFailure = {
                    authFlow = AuthFlow.None
                    signUpViewModel.changeScreen(SignUpViews.VEHICLE_DATA_VIEW)
                }
            ) {
                navigateToMenu(signUpUiState.value.paymentPlan)
                authFlow = AuthFlow.None
            }
        }

        AuthFlow.None -> {}
    }
}

@Composable
fun SignUpStatus(
    ctx: Context,
    onEnableButton: () -> Unit,
    signUpRequestStatus: ApiResult<List<GeneralResponse>?>,
    onFailure: () -> Unit,
    onLogin: () -> Unit
) {
    when (signUpRequestStatus) {
        is ApiResult.Success -> {
            onEnableButton()
            if (signUpRequestStatus.data != null) {
                if (signUpRequestStatus.data[0].id != 200) {
                    Toast.makeText(ctx, signUpRequestStatus.data[0].message, Toast.LENGTH_SHORT).show()
                    onFailure()
                } else onLogin()
            } else Toast.makeText(
                ctx,
                SignUpAlerts.UNKNOWN.message,
                Toast.LENGTH_SHORT
            ).show()
        }

        is ApiResult.Error -> {
            onEnableButton()
            Toast.makeText(
                ctx,
                R.string.signup_network_alert,
                Toast.LENGTH_SHORT
            ).show()
            onFailure()
            Log.e("SingUpScreen", "${signUpRequestStatus.message}")
        }

        ApiResult.Loading -> {}
    }
}

@Composable
fun LoginStatus(
    ctx: Context,
    loginRequestStatus: Result<LoginResponse>,
    onFailure: () -> Unit,
    onNavigate: () -> Unit
) {
    when (loginRequestStatus) {
        is Result.Success -> {
            onNavigate()
        }

        is Result.Failure -> {
            Toast.makeText(
                ctx,
                R.string.signup_network_alert,
                Toast.LENGTH_SHORT
            ).show()
            onFailure()
            Log.e("SingUpScreen", "${loginRequestStatus.exception.message}")
        }

        Result.Loading -> {}
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun SignUpTopBarPreview() {
    HombreCamionTheme {
        UserInfoTopBar(
            modifier = Modifier,
            showNavigateUp = true,
            onNavigateUp = {}
        )
    }
}
