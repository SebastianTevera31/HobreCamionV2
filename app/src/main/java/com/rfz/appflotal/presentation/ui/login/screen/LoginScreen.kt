package com.rfz.appflotal.presentation.ui.login.screen

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.NavScreens
import com.rfz.appflotal.presentation.commons.TermsAndConditionsText
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginEvent
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginUiState
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navController: NavController
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoading by loginViewModel.isLoginEnabled.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            loginViewModel.cleanLoginState()
            loginViewModel.cleanLoginData()
        }
    }

    LaunchedEffect(Unit) {
        loginViewModel.events.collect { event ->
            when (event) {
                LoginEvent.NavigateToHome -> {
                    if (!(uiState as LoginUiState.Success).termsGranted) {
                        navController.navigate(NavScreens.TERMINOS)
                        loginViewModel.cleanLoginData()
                    } else {
                        navController.navigate(NavScreens.HOME) {
                            popUpTo(NavScreens.LOGIN) { inclusive = true }
                        }
                    }
                }

                LoginEvent.NavigateToPermissions -> {
                    navController.navigate(NavScreens.PERMISOS) {
                        popUpTo(NavScreens.LOGIN) { inclusive = true }
                    }
                }

                is LoginEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when (uiState) {
        LoginUiState.Idle -> Unit

        LoginUiState.Loading -> {
            AwaitDialog()
        }

        is LoginUiState.Error -> {
            val message = stringResource((uiState as LoginUiState.Error).message)
            ErrorDialog(message) {
                loginViewModel.cleanLoginState()
                loginViewModel.cleanLoginData()
            }
        }

        is LoginUiState.Success -> Unit
    }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        LoginContent(
            email = email,
            password = password,
            isLoading = isLoading,
            onLoginClick = {
                loginViewModel.onLoginClicked()
            },
            onRegisterClick = {
                loginViewModel.cleanLoginData()
                navController.navigate(NavScreens.REGISTRAR_USUARIO)
            },
            onCleanUserData = {
                loginViewModel.cleanLoginData()
            },
            onEmailChange = {
                loginViewModel.onEmailChanged(it)
            },
            onPasswordChange = {
                loginViewModel.onPasswordChanged(it)
            }
        )
    }
}


@Composable
private fun LoginContent(
    email: String,
    password: String,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onCleanUserData: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF213DF3), // Blue
                        Color(0xFF4CAF50)  // Green
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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
                email = email,
                password = password,
                isLoading = isLoading,
                onLoginClick = onLoginClick,
                onRegisterClick = onRegisterClick,
                onCleanUserData = onCleanUserData,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.Center) {
                TermsAndConditionsText(
                    text = stringResource(R.string.terminos_condiciones),
                    context = context,
                    url = "https://www.flotal.com.mx/terminos-y-condiciones/",

                    )
                Spacer(modifier = Modifier.height(16.dp))
                TermsAndConditionsText(
                    text = stringResource(R.string.politicas_de_privacidad),
                    context = context,
                    url = "https://www.flotal.com.mx/aviso-de-privacidad",
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
    email: String,
    password: String,
    brandColor: Color = Color(0xFF213DF3),
    darkerGray: Color = Color(0xFF333333),
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onCleanUserData: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        onCleanUserData()
    }

    UsernameField(
        value = email,
        onValueChange = { onEmailChange(it) },
        brandColor = brandColor,
        darkerGray = darkerGray
    )

    Spacer(modifier = Modifier.height(16.dp))

    PasswordField(
        value = password,
        onValueChange = { onPasswordChange(it) },
        brandColor = MaterialTheme.colorScheme.primary,
        darkerGray = darkerGray
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.no_tienes_cuenta_registrate),
        color = MaterialTheme.colorScheme.primary,
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

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLoginClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
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
                stringResource(R.string.correo_electr_nico),
                color = darkerGray.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun LoginScreenPreview() {
    HombreCamionTheme {
        LoginContent(
            email = "",
            password = "",
            isLoading = false,
            onLoginClick = {},
            onRegisterClick = {},
            onCleanUserData = {},
            onEmailChange = {}
        ) { }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ErrorLoginDialogPreview() {
    HombreCamionTheme {
        ErrorDialog("Mensaje de error") {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "Ocurrió un problema",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}