package com.rfz.appflotal.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.brand.screen.MarcasScreen
import com.rfz.appflotal.presentation.ui.brand.viewmodel.BrandViewModel
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.marcarenovados.screens.MarcaRenovadosScreen
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.MarcaRenovadosViewModel
import com.rfz.appflotal.presentation.ui.medidasllantasscreen.MedidasLlantasScreen
import com.rfz.appflotal.presentation.ui.montajedesmontajescreen.MontajeDesmontajeScreen
import com.rfz.appflotal.presentation.ui.originaldesign.OriginalScreen
import com.rfz.appflotal.presentation.ui.productoscreen.NuevoProductoScreen
import com.rfz.appflotal.presentation.ui.productoscreen.ProductViewModel
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.NuevoRegistroLlantasScreen
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.NuevoRegistroLlantasViewModel
import com.rfz.appflotal.presentation.ui.registrovehiculosscreen.NuevoRegistroVehiculoScreen
import com.rfz.appflotal.presentation.ui.registrovehiculosscreen.VehicleViewModel
import com.rfz.appflotal.presentation.ui.retreatedesign.screens.RetreatedDesignScreen
import com.rfz.appflotal.presentation.ui.retreatedesign.viewmodel.RetreatedDesignViewModel
import com.rfz.appflotal.domain.brand.BrandCrudUseCase
import com.rfz.appflotal.domain.brand.BrandListUseCase
import com.rfz.appflotal.domain.originaldesign.CrudOriginalDesignUseCase
import com.rfz.appflotal.domain.originaldesign.OriginalDesignByIdUseCase
import com.rfz.appflotal.domain.originaldesign.OriginalDesignUseCase
import com.rfz.appflotal.domain.product.ProductByIdUseCase
import com.rfz.appflotal.domain.product.ProductCrudUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.LoadingCapacityUseCase
import com.rfz.appflotal.domain.tire.TireSizeCrudUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import com.rfz.appflotal.domain.utilization.UtilizationUseCase
import com.rfz.appflotal.domain.base.BaseUseCase
import com.rfz.appflotal.domain.controltype.ControlTypeUseCase
import com.rfz.appflotal.domain.route.RouteUseCase
import com.rfz.appflotal.domain.vehicle.VehicleByIdUseCase
import com.rfz.appflotal.domain.vehicle.VehicleCrudUseCase
import com.rfz.appflotal.domain.vehicle.VehicleListUseCase
import com.rfz.appflotal.domain.vehicle.VehicleTypeUseCase

fun NavGraphBuilder.catalogGraph(
    navController: NavController,
    homeViewModel: HomeViewModel,
    // Temporales hasta que movamos todo a ViewModels
    brandListUseCase: BrandListUseCase,
    brandCrudUseCase: BrandCrudUseCase,
    originalDesignUseCase: OriginalDesignUseCase,
    originalDesignByIdUseCase: OriginalDesignByIdUseCase,
    crudOriginalDesignUseCase: CrudOriginalDesignUseCase,
    utilizationUseCase: UtilizationUseCase,
    tireSizeUseCase: TireSizeUseCase,
    tireSizeCrudUseCase: TireSizeCrudUseCase,
    productListUseCase: ProductListUseCase,
    productCrudUseCase: ProductCrudUseCase,
    productByIdUseCase: ProductByIdUseCase,
    loadingCapacityUseCase: LoadingCapacityUseCase,
    vehicleListUseCase: VehicleListUseCase,
    vehicleCrudUseCase: VehicleCrudUseCase,
    vehicleByIdUseCase: VehicleByIdUseCase,
    vehicleTypeUseCase: VehicleTypeUseCase,
    controlTypeUseCase: ControlTypeUseCase,
    routeUseCase: RouteUseCase,
    baseUseCase: BaseUseCase
) {
    composable(NavScreens.MARCAS) {
        val viewModel: BrandViewModel = hiltViewModel()
        MarcasScreen(
            navController = navController,
            homeViewModel = homeViewModel,
            viewModel = viewModel
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
        val retreatedDesignViewModel: RetreatedDesignViewModel = hiltViewModel()
        RetreatedDesignScreen(
            viewModel = retreatedDesignViewModel,
            onBackScreen = { navController.popBackStack() })
    }

    composable(NavScreens.MARCA_RENOVADA) {
        val marcaRenovadosScreen: MarcaRenovadosViewModel = hiltViewModel()
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
        val viewModel: ProductViewModel = hiltViewModel()
        NuevoProductoScreen(
            navController = navController,
            homeViewModel = homeViewModel,
            viewModel = viewModel
        )
    }

    composable(NavScreens.NUEVO_PRODUCTO) {
        val viewModel: ProductViewModel = hiltViewModel()
        NuevoProductoScreen(
            navController = navController,
            homeViewModel = homeViewModel,
            viewModel = viewModel
        )
    }

    composable(NavScreens.REGISTRO_LLANTAS) {
        val nuevoRegistroLllantasViewModel: NuevoRegistroLlantasViewModel = hiltViewModel()
        NuevoRegistroLlantasScreen(
            navController = navController,
            viewModel = nuevoRegistroLllantasViewModel
        )
    }

    composable(NavScreens.REGISTRO_VEHICULOS) {
        val viewModel: VehicleViewModel = hiltViewModel()
        NuevoRegistroVehiculoScreen(
            navController = navController,
            homeViewModel = homeViewModel,
            viewModel = viewModel
        )
    }

    composable(NavScreens.MONTAJE_DESMONTAJE) {
        MontajeDesmontajeScreen(
            navController
        )
    }
}
