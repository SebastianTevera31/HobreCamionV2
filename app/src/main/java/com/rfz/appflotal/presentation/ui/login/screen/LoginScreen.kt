package com.rfz.appflotal.presentation.ui.login.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Connected
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.core.util.NavScreens.RECUPERAR_CONTRASENIA
import com.rfz.appflotal.core.util.NavScreens.REGISTRAR_USUARIO
import com.rfz.appflotal.data.model.login.response.LoginState
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.ProgressDialog
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        val loginState by loginViewModel.loginState.collectAsState()
        val navigateToHome by loginViewModel.navigateToHome.observeAsState()
        val context = LocalContext.current
        val isProgressVisible by loginViewModel.isProgressVisible.collectAsState()

        LaunchedEffect(loginState) {
            when (val state = loginState) {
                is LoginState.Success -> {
                    if (navigateToHome?.second == PaymentPlanType.Complete) {
                        navController.navigate(NavScreens.HOME) {
                            popUpTo(NavScreens.LOGIN) { inclusive = true }
                        }
                    }
                }

                else -> {}
            }
        }

        if (isProgressVisible) {
            ProgressDialog()
        }

        LoginContent(loginViewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun LoginContent(
    loginViewModel: LoginViewModel,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading: Boolean by loginViewModel.isLoading.observeAsState(initial = false)
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF213DF3), // Blue
                    Color(0xFF4CAF50)  // Green
                )
            )
        )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoImage(modifier = Modifier.size(240.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WelcomeMessage()
                Spacer(modifier = Modifier.height(32.dp))
                LoginForm(
                    loginViewModel = loginViewModel,
                    brandColor = primaryLight,
                    darkerGray = secondaryLight,
                    isLoading = isLoading,
                    onLoginClick = {
                        if (Connected.isConnected(context)) {
                            loginViewModel.onLoginSelected(context)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.error_conexion_internet),
                                    actionLabel = "OK"
                                )
                            }
                        }
                    },
                    onRegisterClick = { navController.navigate(REGISTRAR_USUARIO) },
                    onForgotPasswordClick = {
                        navController.navigate(RECUPERAR_CONTRASENIA)
                    }
                )
            }
        }
    }
}


@Composable
private fun LogoImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.flotal),
            contentDescription = stringResource(R.string.content_description_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun WelcomeMessage() {
    Text(
        text = stringResource(R.string.title_bienvenida),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFFFF)
        ),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LoginForm(
    loginViewModel: LoginViewModel,
    brandColor: Color,
    darkerGray: Color,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val usuario: String by loginViewModel.usuario.observeAsState(initial = "")
    val password: String by loginViewModel.password.observeAsState(initial = "")
    val isLoginEnabled: Boolean by loginViewModel.isLoginEnable.observeAsState(initial = false)
    val loginMessage: String by loginViewModel.loginMessage.observeAsState(initial = "")

    LaunchedEffect(Unit) {
        loginViewModel.cleanLoginData()
    }

    if (loginMessage.isNotEmpty()) {
        Text(
            text = loginMessage,
            color = Color.White,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    UsernameField(
        value = usuario,
        onValueChange = { loginViewModel.onLoginChanged(usuario = it, password = password) },
        brandColor = brandColor,
        darkerGray = darkerGray
    )

    Spacer(modifier = Modifier.height(16.dp))

    PasswordField(
        value = password,
        onValueChange = { loginViewModel.onLoginChanged(usuario = usuario, password = it) },
        brandColor = brandColor,
        darkerGray = darkerGray
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.no_tienes_cuenta_registrate),
        color = brandColor,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRegisterClick)
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Start
    )

    //    Text(
//        text = stringResource(R.string.title_forget),
//        color = brandColor,
//        style = TextStyle(
//            fontSize = 15.sp,
//            fontWeight = FontWeight.Bold
//        ),
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onForgotPasswordClick)
//            .padding(vertical = 8.dp),
//        textAlign = TextAlign.End
//    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLoginClick,
        enabled = isLoginEnabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = brandColor,
            disabledContainerColor = brandColor.copy(alpha = 0.6f),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
            )
        } else {
            Text(
                text = stringResource(R.string.title_ingresar),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsernameField(
    value: String,
    onValueChange: (String) -> Unit,
    brandColor: Color,
    darkerGray: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                stringResource(R.string.title_user),
                color = darkerGray.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = brandColor,
            unfocusedBorderColor = brandColor,
            focusedLabelColor = brandColor,
            cursorColor = brandColor,
            focusedTextColor = Color.DarkGray,
            unfocusedTextColor = Color.DarkGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    brandColor: Color,
    darkerGray: Color
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                stringResource(R.string.title_contraseña),
                color = darkerGray.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = stringResource(R.string.content_description_icon_password),
                    tint = brandColor
                )
            }
        },
        shape = RoundedCornerShape(12.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = brandColor,
            unfocusedBorderColor = brandColor,
            focusedLabelColor = brandColor,
            cursorColor = brandColor,
            focusedTextColor = Color.DarkGray,
            unfocusedTextColor = Color.DarkGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White
        )

    )
}