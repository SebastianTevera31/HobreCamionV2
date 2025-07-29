package com.rfz.appflotal.presentation.ui.productoscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.model.tire.response.LoadingCapacityResponse
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.domain.originaldesign.OriginalDesignUseCase
import com.rfz.appflotal.domain.product.ProductByIdUseCase
import com.rfz.appflotal.domain.product.ProductCrudUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.LoadingCapacityUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoProductoScreen(
    navController: NavController,
    productListUseCase: ProductListUseCase,
    productCrudUseCase: ProductCrudUseCase,
    productByIdUseCase: ProductByIdUseCase,
    originalDesignUseCase: OriginalDesignUseCase,
    tireSizeUseCase: TireSizeUseCase,
    loadingCapacityUseCase: LoadingCapacityUseCase,
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


    var allProducts by remember { mutableStateOf<List<ProductResponse>>(emptyList()) }
    var displayedProducts by remember { mutableStateOf<List<ProductResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }


    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductResponse?>(null) }
    var isLoadingProductDetails by remember { mutableStateOf(false) }


    var showOriginalDesignMenu by remember { mutableStateOf(false) }
    var showTireSizeMenu by remember { mutableStateOf(false) }
    var showLoadCapacityMenu by remember { mutableStateOf(false) }
    val originalDesigns = remember { mutableStateListOf<OriginalDesignResponse>() }
    val tireSizes = remember { mutableStateListOf<TireSizeResponse>() }
    val loadCapacities = remember { mutableStateListOf<LoadingCapacityResponse>() }
    var isLoadingCombos by remember { mutableStateOf(false) }


    var selectedOriginalDesign by remember { mutableStateOf<OriginalDesignResponse?>(null) }
    var selectedTireSize by remember { mutableStateOf<TireSizeResponse?>(null) }
    var selectedLoadCapacity by remember { mutableStateOf<LoadingCapacityResponse?>(null) }
    var treadDepth by remember { mutableStateOf("") }


    fun applyFilter() {
        displayedProducts = if (searchQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.descriptionProduct.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }


    fun loadProducts() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = productListUseCase("Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    allProducts = result.getOrNull() ?: emptyList()
                    displayedProducts = allProducts
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error loading products"
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
                val userId = userData?.id_user ?: 0

                originalDesigns.clear()
                tireSizes.clear()
                loadCapacities.clear()


                val originalDesignsResult = originalDesignUseCase(bearerToken)
                if (originalDesignsResult.isSuccess) {
                    originalDesigns.addAll(originalDesignsResult.getOrNull() ?: emptyList())
                }


                val tireSizesResult = tireSizeUseCase.doTireSizes(userId, bearerToken)
                if (tireSizesResult.isSuccessful) {
                    tireSizesResult.body()?.let { tireSizes.addAll(it) }
                }


                val loadCapacitiesResult = loadingCapacityUseCase.doCapacity(userId, bearerToken)
                if (loadCapacitiesResult.isSuccessful) {
                    loadCapacitiesResult.body()?.let { loadCapacities.addAll(it) }
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



    fun loadProductDetails(productId: Int) {
        scope.launch {
            isLoadingProductDetails = true
            try {
                val result = productByIdUseCase(productId, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    val productDetails = result.getOrNull()?.firstOrNull()
                    productDetails?.let {
                        selectedOriginalDesign = originalDesigns.find { design ->
                            design.idOriginalDesign == productDetails.c_originalDesign_fk_1
                        }
                        selectedTireSize = tireSizes.find { size ->
                            size.id_tireSize == productDetails.c_tireSize_fk_2
                        }
                        selectedLoadCapacity = loadCapacities.find { capacity ->
                            capacity.id_loadingCapacity == productDetails.c_loadCapacity_fk_3
                        }
                        treadDepth = productDetails.fld_treadDepth?.toString() ?: ""
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error loading product details: ${e.message}"
            } finally {
                isLoadingProductDetails = false
            }
        }
    }

    fun saveProduct() {
        scope.launch {
            if (selectedOriginalDesign == null || selectedTireSize == null || selectedLoadCapacity == null || treadDepth.isBlank()) {
                errorMessage = "Todos los campos son requeridos"
                return@launch
            }

            try {
                val request = ProductCrudDto(
                    idProduct = editingProduct?.idProduct ?: 0,
                    originalDesignId = selectedOriginalDesign!!.idOriginalDesign,
                    tireSizeId = selectedTireSize!!.id_tireSize,
                    loadCapacityId = selectedLoadCapacity!!.id_loadingCapacity,
                    treadDepth = treadDepth.toInt()
                )

                val result = productCrudUseCase(request, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    snackbarHostState.showSnackbar(
                        message = result.getOrNull()?.firstOrNull()?.message ?: "Producto guardado exitosamente",
                        duration = SnackbarDuration.Short
                    )
                    showDialog = false
                    loadProducts()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar el producto"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadProducts()
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
                if (success && editingProduct != null) {
                    loadProductDetails(editingProduct!!.idProduct)
                }
            }
        } else {

            selectedOriginalDesign = null
            selectedTireSize = null
            selectedLoadCapacity = null
            treadDepth = ""
            editingProduct = null
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Productos",
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
                    editingProduct = null
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", tint = lightTextColor)
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
                    placeholder = { Text("Buscar productos...", color = Color.White.copy(alpha = 0.6f)) },
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
                    isLoading && displayedProducts.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    displayedProducts.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay productos registrados" else "No se encontraron resultados",
                            modifier = Modifier.align(Alignment.Center),
                            color = textColor.copy(alpha = 0.6f)
                        )
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                            items(displayedProducts) { product ->
                                ProductItem(
                                    product = product,
                                    originalDesigns = originalDesigns,
                                    tireSizes = tireSizes,
                                    loadCapacities = loadCapacities,
                                    onEditClick = {
                                        editingProduct = product
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
                            text = if (editingProduct == null) "Registrar Producto" else "Editar Producto",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    if (isLoadingCombos || (editingProduct != null && isLoadingProductDetails)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    } else {
                        Column {

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Column {
                                    Text(
                                        "Diseño Original",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showOriginalDesignMenu,
                                        onExpandedChange = { showOriginalDesignMenu = !showOriginalDesignMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedOriginalDesign?.let { "${it.description} - ${it.model}" } ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOriginalDesignMenu)
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
                                            expanded = showOriginalDesignMenu,
                                            onDismissRequest = { showOriginalDesignMenu = false }
                                        ) {
                                            originalDesigns.forEach { design ->
                                                DropdownMenuItem(
                                                    text = { Text("${design.description} - ${design.model}") },
                                                    onClick = {
                                                        selectedOriginalDesign = design
                                                        showOriginalDesignMenu = false
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
                                        "Tamaño de Llanta",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showTireSizeMenu,
                                        onExpandedChange = { showTireSizeMenu = !showTireSizeMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedTireSize?.fld_size ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTireSizeMenu)
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
                                            expanded = showTireSizeMenu,
                                            onDismissRequest = { showTireSizeMenu = false }
                                        ) {
                                            tireSizes.forEach { size ->
                                                DropdownMenuItem(
                                                    text = { Text(size.fld_size) },
                                                    onClick = {
                                                        selectedTireSize = size
                                                        showTireSizeMenu = false
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
                                        "Capacidad de Carga",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = showLoadCapacityMenu,
                                        onExpandedChange = { showLoadCapacityMenu = !showLoadCapacityMenu }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedLoadCapacity?.fld_description ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLoadCapacityMenu)
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
                                            expanded = showLoadCapacityMenu,
                                            onDismissRequest = { showLoadCapacityMenu = false }
                                        ) {
                                            loadCapacities.forEach { capacity ->
                                                DropdownMenuItem(
                                                    text = { Text(capacity.fld_description) },
                                                    onClick = {
                                                        selectedLoadCapacity = capacity
                                                        showLoadCapacityMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(
                                    "Profundidad de la Banda (mm)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = primaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = treadDepth,
                                    onValueChange = { treadDepth = it.filter { c -> c.isDigit() } },
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
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveProduct() },
                        enabled = !isLoadingCombos && !isLoadingProductDetails &&
                                selectedOriginalDesign != null &&
                                selectedTireSize != null &&
                                selectedLoadCapacity != null &&
                                treadDepth.isNotBlank(),
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
    }
}

@Composable
fun ProductItem(
    product: ProductResponse,
    originalDesigns: List<OriginalDesignResponse>,
    tireSizes: List<TireSizeResponse>,
    loadCapacities: List<LoadingCapacityResponse>,
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
                text = product.descriptionProduct.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Profundidad: ${product.treadDepth} mm",
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