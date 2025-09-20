package com.rfz.appflotal.presentation.ui.registrovehiculosscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import com.rfz.appflotal.data.model.delete.CatalogDeleteDto
import com.rfz.appflotal.data.model.route.response.RouteResponse
import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import com.rfz.appflotal.data.model.vehicle.response.*
import com.rfz.appflotal.domain.base.BaseUseCase
import com.rfz.appflotal.domain.controltype.ControlTypeUseCase
import com.rfz.appflotal.domain.delete.CatalogDeleteUseCase
import com.rfz.appflotal.domain.route.RouteUseCase
import com.rfz.appflotal.domain.vehicle.*
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRegistroVehiculoScreen(
    navController: NavController,
    vehicleListUseCase: VehicleListUseCase,
    vehicleCrudUseCase: VehicleCrudUseCase,
    vehicleByIdUseCase: VehicleByIdUseCase,
    vehicleTypeUseCase: VehicleTypeUseCase,
    controlTypeUseCase: ControlTypeUseCase,
    routeUseCase: RouteUseCase,
    baseUseCase: BaseUseCase,
    catalogDeleteUseCase: CatalogDeleteUseCase,
    homeViewModel: HomeViewModel
) {

    val primaryColor = Color(0xFF4A3DAD)
    val secondaryColor = Color(0xFF5C4EC9)
    val backgroundColor = Color(0xFFF8F7FF)
    val textColor = Color(0xFF333333)
    val lightTextColor = Color.White

    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var allVehicles by remember { mutableStateOf<List<VehicleListResponse>>(emptyList()) }
    var displayedVehicles by remember { mutableStateOf<List<VehicleListResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var editingVehicle by remember { mutableStateOf<VehicleListResponse?>(null) }
    var isLoadingVehicleDetails by remember { mutableStateOf(false) }

    var showVehicleTypeMenu by remember { mutableStateOf(false) }
    var showControlTypeMenu by remember { mutableStateOf(false) }
    var showRouteMenu by remember { mutableStateOf(false) }
    var showBaseMenu by remember { mutableStateOf(false) }
    val vehicleTypes = remember { mutableStateListOf<TypeVehicleResponse>() }
    val controlTypes = remember { mutableStateListOf<ControlTypeResponse>() }
    val routes = remember { mutableStateListOf<RouteResponse>() }
    val bases = remember { mutableStateListOf<BaseResponse>() }
    var isLoadingCombos by remember { mutableStateOf(false) }

    var selectedVehicleType by remember { mutableStateOf<TypeVehicleResponse?>(null) }
    var spareTires by remember { mutableStateOf("") }
    var selectedControlType by remember { mutableStateOf<ControlTypeResponse?>(null) }
    var selectedRoute by remember { mutableStateOf<RouteResponse?>(null) }
    var selectedBase by remember { mutableStateOf<BaseResponse?>(null) }
    var vehicleNumber by remember { mutableStateOf("") }
    var plates by remember { mutableStateOf("") }
    var dailyMaximumKm by remember { mutableStateOf("") }
    var odometerStartDate by remember { mutableStateOf("") }
    var initialOdometerValue by remember { mutableStateOf("") }
    var averageDailyKilometers by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var vehicleToDelete by remember { mutableStateOf<VehicleListResponse?>(null) }

    fun applyFilter() {
        displayedVehicles = if (searchQuery.isBlank()) {
            allVehicles
        } else {
            allVehicles.filter { vehicle ->
                vehicle.fldVehicleNumber.contains(searchQuery, ignoreCase = true) ||
                        vehicle.fldPlates.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun loadVehicles() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = vehicleListUseCase("Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    allVehicles = result.getOrNull() ?: emptyList()
                    displayedVehicles = allVehicles
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error loading vehicles"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadComboData(onComplete: (Boolean) -> Unit = {}) {
        scope.launch {
            isLoadingCombos = true
            try {
                val bearerToken = "Bearer ${userData?.fld_token ?: ""}"

                vehicleTypes.clear()
                controlTypes.clear()
                routes.clear()
                bases.clear()

                val vehicleTypesResult = vehicleTypeUseCase(bearerToken)
                if (vehicleTypesResult.isSuccess) {
                    vehicleTypes.addAll(vehicleTypesResult.getOrNull() ?: emptyList())
                }

                val controlTypesResult = controlTypeUseCase(bearerToken)
                if (controlTypesResult.isSuccess) {
                    controlTypes.addAll(controlTypesResult.getOrNull() ?: emptyList())
                }

                val routesResult = routeUseCase(bearerToken)
                if (routesResult.isSuccess) {
                    routes.addAll(routesResult.getOrNull() ?: emptyList())
                }

                val basesResult = baseUseCase(bearerToken)
                if (basesResult.isSuccess) {
                    bases.addAll(basesResult.getOrNull() ?: emptyList())
                }

                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
                errorMessage = "Error loading combo data: ${e.message}"
            } finally {
                isLoadingCombos = false
            }
        }
    }

    fun loadVehicleDetails(vehicleId: Int) {
        scope.launch {
            isLoadingVehicleDetails = true
            try {
                val result = vehicleByIdUseCase("Bearer ${userData?.fld_token}" ?: "", vehicleId)
                if (result.isSuccess) {
                    val vehicleDetails = result.getOrNull()?.firstOrNull()
                    vehicleDetails?.let {
                         selectedVehicleType = vehicleTypes.find { type ->
                            type.idTypeVehicle == vehicleDetails.typeVehicleFk
                        }
                        spareTires = vehicleDetails.spareTires.toString()
                        selectedControlType = controlTypes.find { control ->
                            control.idControlType == vehicleDetails.typeControlFk
                        }
                        selectedRoute = routes.find { route ->
                            route.idRoute == vehicleDetails.routeFk
                        }

                        selectedBase = bases.find { base ->
                            base.id_base == vehicleDetails.baseFk
                        }
                        vehicleNumber = vehicleDetails.vehicleNumber
                        plates = vehicleDetails.plates
                        dailyMaximumKm = vehicleDetails.dailyMaximumKm.toString()


                        odometerStartDate = try {
                            val instant = Instant.parse(vehicleDetails.odometerStartDate)
                            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                            localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        } catch (e: Exception) {
                            if (vehicleDetails.odometerStartDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                vehicleDetails.odometerStartDate
                            } else {
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            }
                        }

                        initialOdometerValue = vehicleDetails.initialValueOdometer.toString()
                        averageDailyKilometers = vehicleDetails.averageDailyKilometers.toString()
                    }
                } else {
                    errorMessage = "Error al cargar los detalles del vehículo"
                }
            } catch (e: Exception) {
                errorMessage = "Error loading vehicle details: ${e.message}"
            } finally {
                isLoadingVehicleDetails = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveVehicle() {
        scope.launch {
            if (selectedVehicleType == null || spareTires.isBlank() || selectedControlType == null ||
                selectedRoute == null || selectedBase == null || vehicleNumber.isBlank() ||
                plates.isBlank() || dailyMaximumKm.isBlank() || odometerStartDate.isBlank() ||
                initialOdometerValue.isBlank() || averageDailyKilometers.isBlank()) {
                errorMessage = "Todos los campos son requeridos"
                return@launch
            }

            try {

                val localDate = LocalDate.parse(odometerStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val odometerStartDateTime = localDate.atStartOfDay()
                val zonedDateTimeUTC = odometerStartDateTime.atZone(ZoneOffset.UTC)
                val dateEventOdometerUTC = zonedDateTimeUTC.toInstant()

                val request = VehicleCrudDto(
                    idVehicle = editingVehicle?.idVehicle ?: 0,
                    typeVehicleId = selectedVehicleType!!.idTypeVehicle,
                    spareTires = spareTires.toInt(),
                    typeControlId = selectedControlType!!.idControlType,
                    routeId = selectedRoute!!.idRoute,
                     vehicleNumber = vehicleNumber,
                    plates = plates,
                    dailyMaximumKm = dailyMaximumKm.toInt(),
                    odometerStartDate = dateEventOdometerUTC.toString(),
                    initialOdometerValue = initialOdometerValue.toInt(),
                    averageDailyKilometers = averageDailyKilometers.toInt(),
                    userId = userData?.id_user ?: 0,
                    isActive = true,
                    odometerEvent = 0,
                    dateEventOdometer = dateEventOdometerUTC.toString(),
                    registrationDate = dateEventOdometerUTC.toString()
                )

                val result = vehicleCrudUseCase(request, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {

                    showDialog = false
                    loadVehicles()
                    snackbarHostState.showSnackbar(
                        message = result.getOrNull()?.message ?: "Vehículo guardado exitosamente",
                        duration = SnackbarDuration.Short
                    )
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar el vehículo"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun deleteVehicle() {
        scope.launch {
            vehicleToDelete?.let { vehicle ->
                val dto = CatalogDeleteDto(id = vehicle.idVehicle, table = "vehicle")
                val result = catalogDeleteUseCase(dto, "Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    showDeleteDialog = false
                    loadVehicles()
                    snackbarHostState.showSnackbar(
                        message = "Vehículo eliminado exitosamente",
                        duration = SnackbarDuration.Short
                    )
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al eliminar el vehículo"
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadVehicles()
    }

    LaunchedEffect(searchQuery) {
        applyFilter()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            errorMessage = null
        }
    }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            loadComboData { success ->
                if (success && editingVehicle != null) {
                         loadVehicleDetails(editingVehicle!!.idVehicle)
                }
            }
        } else {
              selectedVehicleType = null
            spareTires = ""
            selectedControlType = null
            selectedRoute = null
            selectedBase = null
            vehicleNumber = ""
            plates = ""
            dailyMaximumKm = ""
            odometerStartDate = ""
            initialOdometerValue = ""
            averageDailyKilometers = ""
            editingVehicle = null
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registro de Vehículos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
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
                            listOf(Color(0xFF6A5DD9), Color(0xFF8E85FF))
                        )
                    )
                    .shadow(4.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingVehicle = null
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Vehicle", tint = lightTextColor)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF6A5DD9), Color(0xFF6D66CB))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.9f)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    },
                    placeholder = { Text("Buscar vehículos...", color = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }

            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
                when {
                    isLoading && displayedVehicles.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    displayedVehicles.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay vehículos registrados" else "No se encontraron resultados",
                            modifier = Modifier.align(Alignment.Center),
                            color = textColor.copy(alpha = 0.6f)
                        )
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                            items(displayedVehicles) { vehicle ->
                                VehicleItem(
                                    vehicle = vehicle,
                                    onEditClick = {
                                        editingVehicle = vehicle
                                        showDialog = true
                                    },
                                    onDeleteClick = {
                                        vehicleToDelete = vehicle
                                        showDeleteDialog = true
                                    },
                                    primaryColor = primaryColor,
                                    secondaryColor = secondaryColor
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                modifier = Modifier.padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White,
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF6A5DD9), Color(0xFF8E85FF))
                                    )
                                )
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = if (editingVehicle == null) "Registrar Vehículo" else "Editar Vehículo",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    if (isLoadingCombos || (editingVehicle != null && isLoadingVehicleDetails)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    } else {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Tipo de Carrocería*",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showVehicleTypeMenu,
                                        onExpandedChange = { showVehicleTypeMenu = !showVehicleTypeMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedVehicleType?.let { "${it.typeVehicle} - ${it.description}" } ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showVehicleTypeMenu)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = showVehicleTypeMenu,
                                            onDismissRequest = { showVehicleTypeMenu = false }
                                        ) {
                                            vehicleTypes.forEach { type ->
                                                DropdownMenuItem(
                                                    text = { Text("${type.typeVehicle} - ${type.description}") },
                                                    onClick = {
                                                        selectedVehicleType = type
                                                        showVehicleTypeMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = spareTires,
                                onValueChange = { spareTires = it.filter { c -> c.isDigit() } },
                                label = { Text("Llanta de Refacción*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Tipo de Control*",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showControlTypeMenu,
                                        onExpandedChange = { showControlTypeMenu = !showControlTypeMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedControlType?.fldDescription ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showControlTypeMenu)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = showControlTypeMenu,
                                            onDismissRequest = { showControlTypeMenu = false }
                                        ) {
                                            controlTypes.forEach { control ->
                                                DropdownMenuItem(
                                                    text = { Text(control.fldDescription) },
                                                    onClick = {
                                                        selectedControlType = control
                                                        showControlTypeMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Ruta*",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showRouteMenu,
                                        onExpandedChange = { showRouteMenu = !showRouteMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedRoute?.description ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRouteMenu)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = showRouteMenu,
                                            onDismissRequest = { showRouteMenu = false }
                                        ) {
                                            routes.forEach { route ->
                                                DropdownMenuItem(
                                                    text = { Text(route.description) },
                                                    onClick = {
                                                        selectedRoute = route
                                                        showRouteMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Base*",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showBaseMenu,
                                        onExpandedChange = { showBaseMenu = !showBaseMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedBase?.fld_description ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBaseMenu)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = showBaseMenu,
                                            onDismissRequest = { showBaseMenu = false }
                                        ) {
                                            bases.forEach { base ->
                                                DropdownMenuItem(
                                                    text = { Text(base.fld_description) },
                                                    onClick = {
                                                        selectedBase = base
                                                        showBaseMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = vehicleNumber,
                                onValueChange = { vehicleNumber = it },
                                label = { Text("Número de Unidad*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            OutlinedTextField(
                                value = plates,
                                onValueChange = { plates = it },
                                label = { Text("Placas*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            OutlinedTextField(
                                value = dailyMaximumKm,
                                onValueChange = { dailyMaximumKm = it.filter { c -> c.isDigit() } },
                                label = { Text("KM Máximo Diario*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            var showDatePicker by remember { mutableStateOf(false) }

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Fecha de registro*",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = odometerStartDate,
                                        onValueChange = { odometerStartDate = it },
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { showDatePicker = true }) {
                                                Icon(Icons.Default.DateRange, contentDescription = "Select date")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = initialOdometerValue,
                                onValueChange = { initialOdometerValue = it.filter { c -> c.isDigit() } },
                                label = { Text("KM Inicial de Registro*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            OutlinedTextField(
                                value = averageDailyKilometers,
                                onValueChange = { averageDailyKilometers = it.filter { c -> c.isDigit() } },
                                label = { Text("Promedio KM Diarios*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )

                            if (showDatePicker) {
                                val datePickerState = rememberDatePickerState()
                                DatePickerDialog(
                                    onDismissRequest = { showDatePicker = false },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                datePickerState.selectedDateMillis?.let {
                                                    val date = Instant.ofEpochMilli(it)
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalDate()
                                                    odometerStartDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                }
                                                showDatePicker = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        OutlinedButton(
                                            onClick = { showDatePicker = false },
                                            border = BorderStroke(1.dp, primaryColor)
                                        ) {
                                            Text("Cancelar", color = primaryColor)
                                        }
                                    }
                                ) {
                                    DatePicker(state = datePickerState)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveVehicle() },
                        enabled = !isLoadingCombos && !isLoadingVehicleDetails &&
                                selectedVehicleType != null &&
                                spareTires.isNotBlank() &&
                                selectedControlType != null &&
                                selectedRoute != null &&
                                selectedBase != null &&
                                vehicleNumber.isNotBlank() &&
                                plates.isNotBlank() &&
                                dailyMaximumKm.isNotBlank() &&
                                odometerStartDate.isNotBlank() &&
                                initialOdometerValue.isNotBlank() &&
                                averageDailyKilometers.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("GUARDAR", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialog = false },
                        border = BorderStroke(1.dp, primaryColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("CANCELAR", color = primaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        "Eliminar Vehículo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar este vehículo?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { deleteVehicle() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteDialog = false },
                        border = BorderStroke(1.dp, primaryColor)
                    ) {
                        Text("CANCELAR", color = primaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun VehicleItem(
    vehicle: VehicleListResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = vehicle.fldVehicleNumber.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Placas: ${vehicle.fldPlates}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = secondaryColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Editar",
                        tint = secondaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }


                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Eliminar",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}