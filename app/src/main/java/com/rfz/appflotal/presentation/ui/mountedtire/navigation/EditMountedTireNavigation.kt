package com.rfz.appflotal.presentation.ui.mountedtire.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rfz.appflotal.presentation.navigation.popBackStackSafely
import com.rfz.appflotal.presentation.ui.mountedtire.EditMountedTireScreen
import com.rfz.appflotal.presentation.ui.mountedtire.viewmodel.EditMountedTireViewModel

/**
 * Pantalla para editar la ficha de una llanta montada (se entra desde el montaje del wizard).
 *
 * Se registra en el NavHost con:  editMountedTireScreen(navController)
 * y se entra con:                 navController.navigate(EditMountedTireRoute(position))
 */
fun NavGraphBuilder.editMountedTireScreen(navController: NavHostController) {
    composable<EditMountedTireRoute> { entry ->
        val args = entry.toRoute<EditMountedTireRoute>()
        val viewModel: EditMountedTireViewModel = hiltViewModel()

        EditMountedTireScreen(
            position = args.position,
            viewModel = viewModel,
            onBack = { navController.popBackStackSafely() }
        )
    }
}
