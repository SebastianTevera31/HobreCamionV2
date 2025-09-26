package com.rfz.appflotal.presentation.ui.registrollantasscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.gson.Gson
import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.domain.acquisitiontype.AcquisitionTypeUseCase
import com.rfz.appflotal.domain.base.BaseUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.provider.ProviderListUseCase
import com.rfz.appflotal.domain.tire.TireCrudUseCase
import com.rfz.appflotal.domain.tire.TireGetUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRegistroLlantasScreen(
    navController: NavController,
    acquisitionTypeUseCase: AcquisitionTypeUseCase,
    providerListUseCase: ProviderListUseCase,
    baseUseCase: BaseUseCase,
    productListUseCase: ProductListUseCase,
    tireCrudUseCase: TireCrudUseCase,
    tireListUsecase: TireListUsecase,
    tireGetUseCase: TireGetUseCase,
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

    var allTires by remember { mutableStateOf<List<TireListResponse>>(emptyList()) }
    var displayedTires by remember { mutableStateOf<List<TireListResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }


    var showDialog by remember { mutableStateOf(false) }
    var editingTire by remember { mutableStateOf<TireListResponse?>(null) }
    var isLoadingTireDetails by remember { mutableStateOf(false) }

    var showAcquisitionTypeMenu by remember { mutableStateOf(false) }
    var showProviderMenu by remember { mutableStateOf(false) }
    var showBaseMenu by remember { mutableStateOf(false) }
    var showProductMenu by remember { mutableStateOf(false) }
    val acquisitionTypes = remember { mutableStateListOf<AcquisitionTypeResponse>() }
    val providers = remember { mutableStateListOf<ProviderListResponse>() }
    val bases = remember { mutableStateListOf<BaseResponse>() }
    val products = remember { mutableStateListOf<ProductResponse>() }
    var isLoadingCombos by remember { mutableStateOf(false) }


    var selectedAcquisitionType by remember { mutableStateOf<AcquisitionTypeResponse?>(null) }
    var selectedProvider by remember { mutableStateOf<ProviderListResponse?>(null) }
    var selectedBase by remember { mutableStateOf<BaseResponse?>(null) }
    var selectedProduct by remember { mutableStateOf<ProductResponse?>(null) }
    var acquisitionDate by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var treadDepth by remember { mutableStateOf("") }
    var tireNumber by remember { mutableStateOf("") }
    var dot by remember { mutableStateOf("") }



    var showDatePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()



    fun applyFilter() {
        displayedTires = if (searchQuery.isBlank()) {
            allTires
        } else {
            allTires.filter { tire ->
                tire.typeAcquisition.contains(searchQuery, ignoreCase = true) ||
                        tire.provider.contains(searchQuery, ignoreCase = true) ||
                        tire.brand.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Load tires
    fun loadTires() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = tireListUsecase("Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    allTires = result.getOrNull() ?: emptyList()
                    displayedTires = allTires
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error loading tires"
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


                acquisitionTypes.clear()
                providers.clear()
                bases.clear()
                products.clear()


                val acquisitionTypeResult = acquisitionTypeUseCase(bearerToken)
                if (acquisitionTypeResult.isSuccess) {
                    acquisitionTypes.add(acquisitionTypeResult.getOrNull() ?: throw Exception("No acquisition types"))
                }


                val providersResult = providerListUseCase(bearerToken, 1)
                if (providersResult.isSuccess) {
                    providers.addAll(providersResult.getOrNull() ?: emptyList())
                }


                val basesResult = baseUseCase(bearerToken)
                if (basesResult.isSuccess) {
                    bases.addAll(basesResult.getOrNull() ?: emptyList())
                }


                val productsResult = productListUseCase(bearerToken)
                if (productsResult.isSuccess) {
                    products.addAll(productsResult.getOrNull() ?: emptyList())
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


    fun loadTireDetails(tireId: Int) {
        scope.launch {
            isLoadingTireDetails = true
            try {
                val result = tireGetUseCase(tireId, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    val tireDetails = result.getOrNull()?.firstOrNull()
                    tireDetails?.let {
                        selectedAcquisitionType = acquisitionTypes.find { type ->
                            type.idAcquisitionType == tireDetails.typeAcquisitionId
                        }
                        selectedProvider = providers.find { provider ->
                            provider.idProvider == tireDetails.providerId
                        }
                        selectedBase = bases.find { base ->
                            base.id_base == tireDetails.destinationId
                        }
                        selectedProduct = products.find { product ->
                            product.idProduct == tireDetails.productId
                        }
                        acquisitionDate = tireDetails.acquisitionDate
                        document = tireDetails.document
                        cost = tireDetails.unitCost.toString()
                        treadDepth = tireDetails.treadDepth.toString()
                        tireNumber = tireDetails.tireNumber
                        dot = tireDetails.dot
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error loading tire details: ${e.message}"
            } finally {
                isLoadingTireDetails = false
            }
        }
    }

    // Save tire
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTire() {
        scope.launch {
            if (selectedAcquisitionType == null || selectedProvider == null ||
                selectedBase == null || selectedProduct == null ||
                acquisitionDate.isBlank() ||
                cost.isBlank() || tireNumber.isBlank()) {
                errorMessage = "Todos los campos requeridos deben estar completos"
                return@launch
            }

            try {

                val dateTime = try {
                    LocalDateTime.parse(acquisitionDate, DateTimeFormatter.ISO_DATE_TIME)
                } catch (e: Exception) {
                     try {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'HH:mm:ss.SSS'Z'")
                        LocalDateTime.parse(acquisitionDate, formatter)
                    } catch (e: Exception) {

                        try {
                            val dateOnly = LocalDate.parse(
                                acquisitionDate.substringBefore('T'),
                                DateTimeFormatter.ISO_DATE
                            )
                            dateOnly.atStartOfDay()
                        } catch (e: Exception) {

                            LocalDateTime.now()
                        }
                    }
                }

                val request = TireCrudDto(
                    idTire = editingTire?.idTire ?: 0,
                    typeAcquisitionId = selectedAcquisitionType!!.idAcquisitionType,
                    providerId = selectedProvider!!.idProvider,
                    acquisitionDate = dateTime.toString(),
                    registrationDate = LocalDateTime.now().toString(),
                    document = "x",
                    treadDepth = treadDepth.toInt(),
                    unitCost = cost.toInt(),
                    tireNumber = tireNumber,
                    userId = userData?.idUser ?: 0,
                    productId = selectedProduct!!.idProduct,
                    dot = dot,
                    isActive = true,
                    retreadDesignId = 0,
                    destinationId = selectedBase!!.id_base,
                    lifecycle = 0
                )

                val gson = Gson()
                val jsonRequest = gson.toJson(request)
                println("JSON Request: $jsonRequest")


                val result = tireCrudUseCase(request, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    snackbarHostState.showSnackbar(
                        message = result.getOrNull()?.message ?: "Llanta guardada exitosamente",
                        duration = SnackbarDuration.Short
                    )
                    showDialog = false
                    loadTires()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar la llanta"
                }
            } catch (e: Exception) {
                errorMessage = "Error al procesar la fecha: ${e.message}"
            }
        }
    }


    LaunchedEffect(Unit) {
        loadTires()
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
                if (success && editingTire != null) {
                    loadTireDetails(editingTire!!.idTire)
                }
            }
        } else {

            selectedAcquisitionType = null
            selectedProvider = null
            selectedBase = null
            selectedProduct = null
            acquisitionDate = ""
            document = ""
            cost = ""
            treadDepth = ""
            tireNumber = ""
            dot = ""
            editingTire = null
        }
    }

    LaunchedEffect(selectedProduct) {
        selectedProduct?.let {
            treadDepth = it.treadDepth.toString()
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registro de Llantas",
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
                    editingTire = null
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tire", tint = lightTextColor)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Search bar
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
                    placeholder = { Text("Buscar llantas...", color = Color.White.copy(alpha = 0.6f)) },
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

            // Tire list
            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
                when {
                    isLoading && displayedTires.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    displayedTires.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay llantas registradas" else "No se encontraron resultados",
                            modifier = Modifier.align(Alignment.Center),
                            color = textColor.copy(alpha = 0.6f)
                        )
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                            items(displayedTires) { tire ->
                                TireItem(
                                    tire = tire,
                                    onEditClick = {
                                        editingTire = tire
                                        showDialog = true
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


        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH) + 1
                                val day = calendar.get(Calendar.DAY_OF_MONTH)

                                acquisitionDate = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}T00:00:00.000Z"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDatePicker = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }


        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Title
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
                            text = if (editingTire == null) "Registrar Llanta" else "Editar Llanta",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))

                        if (isLoadingCombos || (editingTire != null && isLoadingTireDetails)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = primaryColor)
                            }
                        } else {
                            // Acquisition Type field
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Tipo de Compra",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showAcquisitionTypeMenu,
                                        onExpandedChange = { showAcquisitionTypeMenu = !showAcquisitionTypeMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedAcquisitionType?.description ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAcquisitionTypeMenu)
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
                                            expanded = showAcquisitionTypeMenu,
                                            onDismissRequest = { showAcquisitionTypeMenu = false }
                                        ) {
                                            acquisitionTypes.forEach { type ->
                                                DropdownMenuItem(
                                                    text = { Text(type.description) },
                                                    onClick = {
                                                        selectedAcquisitionType = type
                                                        showAcquisitionTypeMenu = false
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
                                        "Proveedor",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showProviderMenu,
                                        onExpandedChange = { showProviderMenu = !showProviderMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedProvider?.description ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProviderMenu)
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
                                            expanded = showProviderMenu,
                                            onDismissRequest = { showProviderMenu = false }
                                        ) {
                                            providers.forEach { provider ->
                                                DropdownMenuItem(
                                                    text = { Text(provider.description) },
                                                    onClick = {
                                                        selectedProvider = provider
                                                        showProviderMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }


                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(
                                    "Fecha de Adquisición",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = acquisitionDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true },
                                    trailingIcon = {
                                        IconButton(onClick = { showDatePicker = true }) {
                                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = primaryColor,
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }


                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Base",
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


                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Producto",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showProductMenu,
                                        onExpandedChange = { showProductMenu = !showProductMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedProduct?.descriptionProduct ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProductMenu)
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
                                            expanded = showProductMenu,
                                            onDismissRequest = { showProductMenu = false }
                                        ) {
                                            products.forEach { product ->
                                                DropdownMenuItem(
                                                    text = { Text(product.descriptionProduct) },
                                                    onClick = {
                                                        selectedProduct = product
                                                        showProductMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(
                                    "Costo $ (SIN IVA)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = cost,
                                    onValueChange = { cost = it.filter { c -> c.isDigit() } },
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
                            }


                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(
                                    "Profundidad de Piso",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = treadDepth,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = textColor,
                                        disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                                        disabledContainerColor = Color.LightGray.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(
                                    "Llanta",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = tireNumber,
                                    onValueChange = { tireNumber = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = primaryColor,
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }


                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(
                                    "DOT",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = dot,
                                    onValueChange = { dot = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = primaryColor,
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { showDialog = false },
                                    border = BorderStroke(1.dp, primaryColor),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("CANCELAR", color = primaryColor, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { saveTire() },
                                    enabled = !isLoadingCombos && !isLoadingTireDetails &&
                                            selectedAcquisitionType != null &&
                                            selectedProvider != null &&
                                            selectedBase != null &&
                                            selectedProduct != null &&
                                            acquisitionDate.isNotBlank() &&

                                            cost.isNotBlank() &&
                                            tireNumber.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("GUARDAR", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TireItem(
    tire: TireListResponse,
    onEditClick: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Llanta #${tire.idTire}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Proveedor: ${tire.provider}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
            )
            Text(
                text = "Marca: ${tire.brand}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
            )
            Text(
                text = "Modelo: ${tire.model}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(secondaryColor.copy(alpha = 0.1f))
                    .clickable(onClick = onEditClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.End)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = secondaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Editar",
                        color = secondaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}