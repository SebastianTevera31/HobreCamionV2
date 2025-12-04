package com.rfz.appflotal.presentation.ui.productoscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
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
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
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

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
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

    val context = LocalContext.current


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
                    errorMessage = context.getString(R.string.error_loading_products)
                }
            } catch (e: Exception) {
                errorMessage = context.getString(R.string.error_desconocido)
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
                val userId = userData?.idUser ?: 0

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
                errorMessage = context.getString(R.string.error_carga_datos)
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
                errorMessage = context.getString(R.string.error_loading_product_details)
            } finally {
                isLoadingProductDetails = false
            }
        }
    }

    fun saveProduct() {
        scope.launch {
            if (selectedOriginalDesign == null || selectedTireSize == null || selectedLoadCapacity == null || treadDepth.isBlank()) {
                errorMessage = context.getString(R.string.error_deben_completarse_campos)
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
                        message = result.getOrNull()?.firstOrNull()?.message
                            ?: context.getString(R.string.producto_guardado_exitosamente),
                        duration = SnackbarDuration.Short
                    )
                    showDialog = false
                    loadProducts()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                        ?: context.getString(R.string.error_al_guardar_el_producto)
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
                        stringResource(R.string.products),
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
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    null,
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.buscar_productos),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
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
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                when {
                    isLoading && displayedProducts.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    displayedProducts.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isBlank()) stringResource(R.string.no_hay_productos_registrados) else stringResource(
                                R.string.no_se_encontraron_resultados
                            ),
                            modifier = Modifier.align(Alignment.Center),
                            color = textColor.copy(alpha = 0.6f)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
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
                    LocalizedApp {
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
                                text = if (editingProduct == null) stringResource(R.string.registrar_producto) else stringResource(
                                    R.string.editar_producto
                                ),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
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
                        LocalizedApp {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            stringResource(R.string.original_design),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = primaryColor,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = showOriginalDesignMenu,
                                            onExpandedChange = {
                                                showOriginalDesignMenu = !showOriginalDesignMenu
                                            }
                                        ) {
                                            OutlinedTextField(
                                                value = selectedOriginalDesign?.let { "${it.description} - ${it.model}" }
                                                    ?: "",
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = showOriginalDesignMenu
                                                    )
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
                                                onDismissRequest = {
                                                    showOriginalDesignMenu = false
                                                }
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

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            stringResource(R.string.tamano_de_llanta),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = primaryColor,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = showTireSizeMenu,
                                            onExpandedChange = {
                                                showTireSizeMenu = !showTireSizeMenu
                                            }
                                        ) {
                                            OutlinedTextField(
                                                value = selectedTireSize?.fld_size ?: "",
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = showTireSizeMenu
                                                    )
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


                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            stringResource(R.string.capacidad_de_carga),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = primaryColor,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = showLoadCapacityMenu,
                                            onExpandedChange = {
                                                showLoadCapacityMenu = !showLoadCapacityMenu
                                            }
                                        ) {
                                            OutlinedTextField(
                                                value = selectedLoadCapacity?.fld_description ?: "",
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = showLoadCapacityMenu
                                                    )
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

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.profundidad_de_la_banda_mm),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = treadDepth,
                                        onValueChange = {
                                            treadDepth = it.filter { c -> c.isDigit() }
                                        },
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
                    }
                },
                confirmButton = {
                    LocalizedApp {
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
                            Text(stringResource(R.string.guardar), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    LocalizedApp {
                        OutlinedButton(
                            onClick = { showDialog = false },
                            border = BorderStroke(1.dp, primaryColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                stringResource(R.string.cancelar),
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                text = stringResource(R.string.profundidad_mm, product.treadDepth),
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
                        pluralStringResource(R.plurals.editar_elemento, 1),
                        color = secondaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}