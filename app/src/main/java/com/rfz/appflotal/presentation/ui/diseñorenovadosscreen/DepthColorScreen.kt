package com.rfz.appflotal.presentation.ui.diseñorenovadosscreen

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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.depthcolor.dto.CreateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.dto.UpdateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.DepthColorResponse
import com.rfz.appflotal.domain.depthcolor.*
import com.rfz.appflotal.presentation.theme.backgroundColor
import com.rfz.appflotal.presentation.theme.lightTextColor
import com.rfz.appflotal.presentation.theme.primaryColor
import com.rfz.appflotal.presentation.theme.secondaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepthColorScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    depthColorUseCase: DepthColorUseCase,
    createDepthColorUseCase: CreateDepthColorUseCase,
    updateDepthColorUseCase: UpdateDepthColorUseCase
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val scope = rememberCoroutineScope()

    var allItems by remember { mutableStateOf<List<DepthColorResponse>>(emptyList()) }
    var displayedItems by remember { mutableStateOf<List<DepthColorResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<DepthColorResponse?>(null) }
    var description by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    var max by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var expandedColorMenu by remember { mutableStateOf(false) }
    val colorOptions = listOf("Rojo", "Amarillo", "Verde")

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allItems else allItems.filter {
            it.description.contains(searchQuery, ignoreCase = true)
        }
        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedItems = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadItems() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = depthColorUseCase("Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    allItems = result.getOrNull() ?: emptyList()
                    resetPagination()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar items"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveItem() {
        scope.launch {
            if (editingItem == null) {

                val request = CreateDepthColorRequest(
                    description = description,
                    min = min.toDoubleOrNull() ?: 0.0,
                    max = max.toDoubleOrNull() ?: 0.0,
                    color = color
                )
                val result = createDepthColorUseCase("Bearer ${userData?.fld_token}", request)
                if (result.isSuccess) {
                    showDialog = false
                    loadItems()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                }
            } else {

                val request = UpdateDepthColorRequest(
                    idDepthColor = editingItem!!.idDepthColor,
                    description = description,
                    min = min.toDoubleOrNull() ?: 0.0,
                    max = max.toDoubleOrNull() ?: 0.0,
                    color = color
                )
                val result = updateDepthColorUseCase("Bearer ${userData?.fld_token}", request)
                if (result.isSuccess) {
                    showDialog = false
                    loadItems()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar"
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadItems() }
    LaunchedEffect(searchQuery) { resetPagination() }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Depth Colors",
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
                placeholder = { Text("Buscar...", color = Color.White.copy(alpha = 0.6f)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF6A5DD9), Color(0xFF6D66CB)))
                    )
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
                if (isLoading && displayedItems.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(displayedItems) { item ->
                        DepthColorItem(
                            item = item,
                            onEditClick = {
                                editingItem = item
                                description = item.description
                                min = item.min.toString()
                                max = item.max.toString()
                                color = item.color
                                expandedColorMenu = false
                                showDialog = true
                            },
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor
                        )
                    }

                    if (showLoadMoreButton) {
                        item {
                            OutlinedButton(
                                onClick = { currentPage++; applyFilterAndPagination() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
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
                onDismissRequest = { },
                modifier = Modifier.padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White,
                tonalElevation = 12.dp,
                title = {
                    Text(
                        if (editingItem == null) "Registrar Depth Color" else "Editar Depth Color",
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
                            onValueChange = { if (editingItem == null) description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = editingItem == null,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.DarkGray,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledPlaceholderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = min,
                            onValueChange = { min = it },
                            label = { Text("Mínimo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = max,
                            onValueChange = { max = it },
                            label = { Text("Máximo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                    /*    Box {
                            OutlinedTextField(
                                value = color,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Color") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedColorMenu = true },
                                trailingIcon = {
                                    Icon(
                                        if (expandedColorMenu) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = expandedColorMenu,
                                onDismissRequest = { expandedColorMenu = false }
                            ) {
                                colorOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            color = option
                                            expandedColorMenu = false
                                        }
                                    )
                                }
                            }
                        }*/
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveItem() },
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
    }
}

@Composable
fun DepthColorItem(
    item: DepthColorResponse,
    onEditClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    val displayColor = try {
        Color(android.graphics.Color.parseColor(item.color))
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Min: ${item.min}", color = Color.Gray, fontSize = 13.sp)
                        Text("Max: ${item.max}", color = Color.Gray, fontSize = 13.sp)
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(displayColor)
                        )
                    }
                }
                IconButton(
                    onClick = { onEditClick() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(secondaryColor.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = secondaryColor)
                }
            }
        }
    }
}
