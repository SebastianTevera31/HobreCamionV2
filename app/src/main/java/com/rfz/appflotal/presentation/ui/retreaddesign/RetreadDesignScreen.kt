package com.rfz.appflotal.presentation.ui.retreaddesign

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.delete.CatalogDeleteDto
import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignResponse
import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.domain.delete.CatalogDeleteUseCase
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandListUseCase
import com.rfz.appflotal.domain.retreaddesign.RetreadDesignListUseCase
import com.rfz.appflotal.domain.retreaddesign.RetreadDesignCrudUseCase
import com.rfz.appflotal.domain.utilization.UtilizationUseCase
import com.rfz.appflotal.presentation.theme.backgroundColor
import com.rfz.appflotal.presentation.theme.lightTextColor
import com.rfz.appflotal.presentation.theme.primaryColor
import com.rfz.appflotal.presentation.theme.secondaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetreadDesignScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    retreadDesignListUseCase: RetreadDesignListUseCase,
    retreadDesignCrudUseCase: RetreadDesignCrudUseCase,
    retreadBrandListUseCase: RetreadBrandListUseCase,
    utilizationUseCase: UtilizationUseCase,
    catalogDeleteUseCase: CatalogDeleteUseCase
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var allDesigns by remember { mutableStateOf<List<RetreadDesignResponse>>(emptyList()) }
    var displayedDesigns by remember { mutableStateOf<List<RetreadDesignResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingDesign by remember { mutableStateOf<RetreadDesignResponse?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedBrandId by remember { mutableStateOf<Int?>(null) }
    var selectedUtilizationId by remember { mutableStateOf<Int?>(null) }
    var treadDepth by remember { mutableStateOf("") }

    var brandList by remember { mutableStateOf<List<RetreadBrandListResponse>>(emptyList()) }
    var utilizationList by remember { mutableStateOf<List<UtilizationResponse>>(emptyList()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var designToDelete by remember { mutableStateOf<RetreadDesignResponse?>(null) }

    fun loadCombos() {
        scope.launch {
            retreadBrandListUseCase("Bearer ${userData?.fld_token}").onSuccess { brands ->
                brandList = brands
            }
            utilizationUseCase("Bearer ${userData?.fld_token}").onSuccess { utils ->
                utilizationList = utils
            }
        }
    }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allDesigns else allDesigns.filter {
            it.description.contains(searchQuery, ignoreCase = true)
        }
        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedDesigns = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadDesigns() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = retreadDesignListUseCase("Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    allDesigns = result.getOrNull() ?: emptyList()
                    resetPagination()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar datos"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveDesign() {
        scope.launch {
            val dto = RetreadDesignCrudDto(
                idRetreadDesign = editingDesign?.id ?: 0,
                description = description,
                retreadBrandId = selectedBrandId ?: 0,
                utilizationId = selectedUtilizationId ?: 0,
                treadDepth = treadDepth.toIntOrNull() ?: 0
            )
            val result = retreadDesignCrudUseCase(dto, "Bearer ${userData?.fld_token}")
            if (result.isSuccess) {
                showDialog = false
                loadDesigns()
                snackbarHostState.showSnackbar(
                    message = if (editingDesign == null) "Diseño registrado exitosamente" else "Diseño actualizado exitosamente",
                    duration = SnackbarDuration.Short
                )
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar/actualizar"
            }
        }
    }

    fun deleteDesign() {
        scope.launch {
            designToDelete?.let { design ->
                val dto = CatalogDeleteDto(
                    id = design.id,
                    table = "retreadDesign"
                )
                val result = catalogDeleteUseCase(dto, "Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    showDeleteDialog = false
                    loadDesigns()
                    snackbarHostState.showSnackbar(
                        message = "Diseño eliminado exitosamente",
                        duration = SnackbarDuration.Short
                    )
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al eliminar el diseño"
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadDesigns()
        loadCombos()
    }

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
                        "Retread Designs",
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
                    editingDesign = null
                    description = ""
                    selectedBrandId = null
                    selectedUtilizationId = null
                    treadDepth = ""
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
                if (isLoading && displayedDesigns.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(displayedDesigns) { design ->
                        RetreadDesignItem(
                            design = design,
                            onEditClick = {
                                editingDesign = design
                                description = design.description
                                selectedBrandId = brandList.find { it.description == design.retreadBrand }?.idRetreadBrand
                                selectedUtilizationId = utilizationList.find { it.fld_description == design.utilization }?.id_utilization
                                treadDepth = design.treadDepth.toString()
                                showDialog = true
                            },
                            onDeleteClick = {
                                designToDelete = design
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
                        if (editingDesign == null) "Registrar Retread Design" else "Editar Retread Design",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF4A3DAD),
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { if (editingDesign == null) description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = editingDesign == null,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.DarkGray,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledPlaceholderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        var expandedBrand by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedBrand,
                            onExpandedChange = { expandedBrand = !expandedBrand }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = brandList.find { it.idRetreadBrand == selectedBrandId }?.description ?: "",
                                onValueChange = { },
                                label = { Text("Retread Brand") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrand) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedBrand,
                                onDismissRequest = { expandedBrand = false }
                            ) {
                                brandList.forEach { brand ->
                                    DropdownMenuItem(
                                        text = { Text(brand.description) },
                                        onClick = {
                                            selectedBrandId = brand.idRetreadBrand
                                            expandedBrand = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        var expandedUtil by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedUtil,
                            onExpandedChange = { expandedUtil = !expandedUtil }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = utilizationList.find { it.id_utilization == selectedUtilizationId }?.fld_description ?: "",
                                onValueChange = { },
                                label = { Text("Utilization") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUtil) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedUtil,
                                onDismissRequest = { expandedUtil = false }
                            ) {
                                utilizationList.forEach { util ->
                                    DropdownMenuItem(
                                        text = { Text(util.fld_description) },
                                        onClick = {
                                            selectedUtilizationId = util.id_utilization
                                            expandedUtil = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = treadDepth,
                            onValueChange = { treadDepth = it },
                            label = { Text("Tread Depth") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveDesign() },
                        enabled = description.isNotBlank() && selectedBrandId != null && selectedUtilizationId != null,
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
                        "Eliminar Diseño",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar el diseño '${designToDelete?.description}'?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { deleteDesign() },
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
fun RetreadDesignItem(
    design: RetreadDesignResponse,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = design.description,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = primaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Brand: ${design.retreadBrand} • Utilization: ${design.utilization} • Depth: ${design.treadDepth}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }


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