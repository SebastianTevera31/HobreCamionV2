package com.rfz.appflotal.presentation.navigation

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.screen.InicioScreen
import com.rfz.appflotal.presentation.ui.inicio.viewmodel.InicioScreenViewModel
import com.rfz.appflotal.presentation.ui.loading.screen.SplashScreen
import com.rfz.appflotal.presentation.ui.login.screen.LoginScreen
import com.rfz.appflotal.presentation.ui.login.viewmodel.LoginViewModel
import com.rfz.appflotal.presentation.ui.password.screen.PasswordScreen
import com.rfz.appflotal.presentation.ui.password.viewmodel.PasswordViewModel
import com.rfz.appflotal.presentation.ui.permission.PermissionScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screen.SignUpScreen
import com.rfz.appflotal.presentation.ui.registrousuario.screen.TerminosScreen
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel
import com.rfz.appflotal.presentation.ui.inicio.ui.arePermissionsGranted
import com.rfz.appflotal.presentation.ui.inicio.ui.getRequiredPermissions
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

fun NavGraphBuilder.authGraph(
    navController: NavController,
    loginViewModel: LoginViewModel,
    inicioScreenViewModel: InicioScreenViewModel,
    homeViewModel: HomeViewModel,
    allGranted: Boolean,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    composable(NavScreens.LOADING) { SplashScreen() }

    composable(NavScreens.LOGIN) {
        LoginScreen(
            loginViewModel = loginViewModel,
            navController = navController
        )
    }

    composable(NavScreens.RECUPERAR_CONTRASENIA) {
        val passwordViewModel: PasswordViewModel = hiltViewModel()
        PasswordScreen(passwordViewModel)
    }

    composable(route = NavScreens.REGISTRAR_USUARIO) {
        val signUpViewModel: SignUpViewModel = hiltViewModel()
        val homeUiState = homeViewModel.uiState.collectAsState()
        val context = navController.context
        
        SignUpScreen(
            navController,
            languageSelected = homeUiState.value.selectedLanguage,
            signUpViewModel = signUpViewModel
        ) {
            val permissionsGranted = arePermissionsGranted(
                context,
                getRequiredPermissions()
            )
            if (!permissionsGranted) {
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

    composable(route = NavScreens.PERMISOS) {
        PermissionScreen(
            context = navController.context,
            allGranted = allGranted,
            launcher = permissionLauncher,
            onGranted = {
                navController.navigate(NavScreens.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            })
    }

    composable(route = NavScreens.TERMINOS) {
        val context = navController.context
        TerminosScreen(
            context = context,
            buttonText = R.string.confirmar,
            onBack = {
                inicioScreenViewModel.deleteUserData()
                navController.popBackStack()
            }) {
            loginViewModel.acceptTermsConditions(onNavigate = {
                navController.navigate(it)
            }) {
                !arePermissionsGranted(
                    context, getRequiredPermissions()
                )
            }
        }
    }

    composable(NavScreens.INICIO) { InicioScreen(navController) }
}
