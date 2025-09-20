package com.rfz.appflotal.presentation.ui.retreadbrand

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.delete.CatalogDeleteDto
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.domain.delete.CatalogDeleteUseCase
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandListUseCase
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandCrudUseCase
import com.rfz.appflotal.presentation.theme.backgroundColor
import com.rfz.appflotal.presentation.theme.lightTextColor
import com.rfz.appflotal.presentation.theme.primaryColor
import com.rfz.appflotal.presentation.theme.secondaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetreadBrandScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    retreadBrandListUseCase: RetreadBrandListUseCase,
    retreadBrandCrudUseCase: RetreadBrandCrudUseCase,
    catalogDeleteUseCase: CatalogDeleteUseCase
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var brandList by remember { mutableStateOf<List<RetreadBrandListResponse>>(emptyList()) }
    var displayedBrands by remember { mutableStateOf<List<RetreadBrandListResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }
    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val token = "Bearer ${userData?.fld_token}"

    var showDialog by remember { mutableStateOf(false) }
    var editingBrand by remember { mutableStateOf<RetreadBrandListResponse?>(null) }
    var description by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var brandToDelete by remember { mutableStateOf<RetreadBrandListResponse?>(null) }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) brandList else brandList.filter {
            it.description.contains(searchQuery, ignoreCase = true)
        }
        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedBrands = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadBrands() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                retreadBrandListUseCase(token).onSuccess { list ->
                    brandList = list
                    resetPagination()
                }.onFailure {
                    errorMessage = it.message ?: "Error al cargar marcas"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveBrand() {
        scope.launch {
            val dto = RetreadBrandDto(
                idRetreadBrand = editingBrand?.idRetreadBrand ?: 0,
                description = description
            )
            retreadBrandCrudUseCase(dto, token).onSuccess { response ->

                showDialog = false
                description = ""
                editingBrand = null
                loadBrands()
                snackbarHostState.showSnackbar(
                    message = response.firstOrNull()?.message ?: "Operación realizada exitosamente",
                    duration = SnackbarDuration.Short
                )
            }.onFailure {
                errorMessage = it.message ?: "Error al guardar/actualizar"
            }
        }
    }

    fun deleteBrand() {
        scope.launch {
            brandToDelete?.let { brand ->
                val dto = CatalogDeleteDto(
                    id = brand.idRetreadBrand,
                    table = "retreadBrand"
                )
                catalogDeleteUseCase(dto, token).onSuccess { response ->
                    showDeleteDialog = false
                    loadBrands()
                    snackbarHostState.showSnackbar(
                        message = "Marca eliminada exitosamente",
                        duration = SnackbarDuration.Short
                    )
                }.onFailure {
                    errorMessage = it.message ?: "Error al eliminar la marca"
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadBrands() }
    LaunchedEffect(searchQuery) { resetPagination() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            errorMessage = null
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Retread Brands",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF6A5DD9), Color(0xFF8E85FF)))
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingBrand = null
                    description = ""
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = lightTextColor)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.White)
                        }
                    }
                },
                placeholder = { Text("Buscar por descripción...", color = Color.White.copy(alpha = 0.6f)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF6A5DD9), Color(0xFF6D66CB))))
                    .padding(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
            )

            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
                if (isLoading && displayedBrands.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(displayedBrands) { brand ->
                        RetreadBrandItem(
                            brand = brand,
                            onEditClick = {
                                editingBrand = brand
                                description = brand.description
                                showDialog = true
                            },
                            onDeleteClick = {
                                brandToDelete = brand
                                showDeleteDialog = true
                            },
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor
                        )
                    }

                    if (showLoadMoreButton) {
                        item {
                            OutlinedButton(
                                onClick = { loadMoreItems() },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(primaryColor, secondaryColor)))
                            ) {
                                Text("Cargar más", fontWeight = FontWeight.Medium)
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
                tonalElevation = 12.dp,
                title = {
                    Text(
                        if (editingBrand == null) "Registrar Retread Brand" else "Editar Retread Brand",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF4A3DAD),
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { saveBrand() },
                        enabled = description.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("GUARDAR", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDialog = false }) {
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
                        "Eliminar Marca",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar la marca '${brandToDelete?.description}'?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { deleteBrand() },
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
fun RetreadBrandItem(
    brand: RetreadBrandListResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = brand.description,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = primaryColor,
                modifier = Modifier.weight(1f)
            )


            IconButton(
                onClick = { onEditClick() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = secondaryColor
                )
            }


            IconButton(
                onClick = { onDeleteClick() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}