package com.rfz.appflotal.presentation.ui.registrollantasscreen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.EstadoErrorCarga
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.EstadoListaVacia
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.EstadoSinResultados
import com.rfz.appflotal.presentation.ui.registrollantasscreen.components.TarjetaLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.EventoListaLlantas
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.NuevoRegistroLlantasUiState
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.NuevoRegistroLlantasViewModel

@Composable
fun NuevoRegistroLlantasScreen(
    onBackClick: () -> Unit,
    onNuevaLlanta: () -> Unit,
    onEditarLlanta: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NuevoRegistroLlantasViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val mensajeGuardada = stringResource(R.string.rl_llanta_guardada)

    LaunchedEffect(viewModel) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                EventoListaLlantas.LlantaGuardada -> snackbarHostState.showSnackbar(mensajeGuardada)
            }
        }
    }

    NuevoRegistroLlantasContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onClearQuery = viewModel::onClearQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onNuevaLlanta = onNuevaLlanta,
        onEditarLlanta = onEditarLlanta,
        onReintentar = viewModel::onReintentar,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRegistroLlantasContent(
    uiState: NuevoRegistroLlantasUiState,
    onBackClick: () -> Unit,
    onClearQuery: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onNuevaLlanta: () -> Unit,
    onEditarLlanta: (Int) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.registro_de_llantas),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.regresar),
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .shadow(4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    SearchBar(
                        uiState.searchQuery,
                        onClearSearchQuery = onClearQuery,
                        onQueryChanged = onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaLlanta,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.agregar_llanta)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        val contenidoModifier = Modifier.padding(paddingValues)
        when {
            uiState.isLoading && uiState.tires.isEmpty() ->
                Box(contenidoModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            uiState.errorCarga && uiState.tires.isEmpty() -> EstadoErrorCarga(
                mensaje = stringResource(R.string.rl_error_cargar_lista),
                onReintentar = onReintentar,
                modifier = contenidoModifier
            )

            uiState.tires.isEmpty() -> EstadoListaVacia(onRegistrar = onNuevaLlanta, modifier = contenidoModifier)

            uiState.displayedTires.isEmpty() ->
                EstadoSinResultados(busqueda = uiState.searchQuery, modifier = contenidoModifier)

            else -> TireList(
                tires = uiState.displayedTires,
                onEditar = onEditarLlanta,
                modifier = contenidoModifier
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onClearSearchQuery: () -> Unit,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.buscar),
                tint = Color.White,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearSearchQuery) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.limpiar),
                        tint = Color.White
                    )
                }
            }
        },
        placeholder = {
            Text(
                stringResource(R.string.rl_buscar_llantas),
                color = Color.White
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.4f
            ),
            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.1f
            ),
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.1f
            )
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
private fun TireList(
    tires: List<TireListResponse>,
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Espacio inferior para que el botón flotante no tape la última tarjeta.
    LazyColumn(
        contentPadding = PaddingValues(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            top = Dimens.PaddingMedium,
            bottom = 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
        modifier = modifier.fillMaxSize()
    ) {
        items(tires, key = { it.idTire }) { tire ->
            TarjetaLlanta(
                marca = tire.brand,
                modelo = tire.model,
                medida = tire.size,
                tipoAdquisicion = tire.typeAcquisition,
                idLlanta = tire.idTire,
                onClick = { onEditar(tire.idTire) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoRegistroLlantasScreenPreview() {
    val sampleTires = listOf(
        TireListResponse(
            idTire = 1,
            provider = "Michelin Provider",
            size = "295/80R22.5",
            brand = "Michelin",
            model = "X Multi Z",
            loadingCapacity = "3550 kg",
            destination = "Dirección",
            typeAcquisition = "Compra",
            lastMountedPosition = "Eje 1 Izq",
            descriptionLastRenovatedDesign = "N/A",
            lastMountedPositionDate = "2023-01-15",
            vehicleNumber = "TR-01",
            dateEventAssembly = "2023-01-15",
            dateEventA = "2023-01-15",
            treadDepthAssembly = 16.0,
            odometerAssembly = 120000,
            typeVehicle = "Camión"
        ),
        TireListResponse(
            idTire = 2,
            provider = "Bridgestone Provider",
            size = "295/80R22.5",
            brand = "Bridgestone",
            model = "R249",
            loadingCapacity = "3550 kg",
            destination = "Tracción",
            typeAcquisition = "Compra",
            lastMountedPosition = "Eje 2 Der",
            descriptionLastRenovatedDesign = "N/A",
            lastMountedPositionDate = "2023-02-20",
            vehicleNumber = "TR-02",
            dateEventAssembly = "2023-02-20",
            dateEventA = "2023-02-20",
            treadDepthAssembly = 15.5,
            odometerAssembly = 95000,
            typeVehicle = "Camión"
        )
    )
    HombreCamionTheme {
        NuevoRegistroLlantasContent(
            uiState = NuevoRegistroLlantasUiState(
                tires = sampleTires,
                displayedTires = sampleTires,
                isLoading = false
            ),
            onBackClick = {},
            onClearQuery = {},
            onSearchQueryChanged = {},
            onNuevaLlanta = {},
            onEditarLlanta = {},
            onReintentar = {}
        )
    }
}
