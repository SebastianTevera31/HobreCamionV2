package com.rfz.appflotal.presentation.ui.defaultparameter


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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.defaultparameter.dto.CreateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.dto.UpdateDefaultParameterRequest

import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.domain.defaultparameter.CreateDefaultParameterUseCase
import com.rfz.appflotal.domain.defaultparameter.DefaultParameterUseCase
import com.rfz.appflotal.domain.defaultparameter.UpdateDefaultParameterUseCase
import com.rfz.appflotal.presentation.theme.backgroundColor
import com.rfz.appflotal.presentation.theme.lightTextColor
import com.rfz.appflotal.presentation.theme.primaryColor
import com.rfz.appflotal.presentation.theme.secondaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultParameterScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    defaultParameterUseCase: DefaultParameterUseCase,
    createDefaultParameterUseCase: CreateDefaultParameterUseCase,
    updateDefaultParameterUseCase: UpdateDefaultParameterUseCase
) {

    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val scope = rememberCoroutineScope()

    var allParams by remember { mutableStateOf<List<DefaultParameterResponse>>(emptyList()) }
    var displayedParams by remember { mutableStateOf<List<DefaultParameterResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingParam by remember { mutableStateOf<DefaultParameterResponse?>(null) }
    var description by remember { mutableStateOf("") }
    var currentValue by remember { mutableStateOf("") }
    var previousValue by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allParams else allParams.filter {
            it.description.contains(searchQuery, ignoreCase = true) ||
                    it.notes?.contains(searchQuery, ignoreCase = true) ?: false
        }
        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedParams = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadParams() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = defaultParameterUseCase("Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    allParams = result.getOrNull() ?: emptyList()
                    resetPagination()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar parámetros"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveParam() {
        scope.launch {
            if (editingParam == null) {

                val request = CreateDefaultParameterRequest(
                    description = description,
                    currentValue = currentValue.toIntOrNull() ?: 0,
                    previousValue = previousValue.toIntOrNull() ?: 0,
                    notes = notes
                )
                val result = createDefaultParameterUseCase("Bearer ${userData?.fld_token}", request)
                if (result.isSuccess) {
                    showDialog = false
                    loadParams()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                }
            } else {

                val request = UpdateDefaultParameterRequest(
                    idParameter = editingParam!!.idParameter,
                    currentValue = currentValue.toIntOrNull() ?: 0
                )
                val result = updateDefaultParameterUseCase("Bearer ${userData?.fld_token}", request)
                if (result.isSuccess) {
                    showDialog = false
                    loadParams()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar"
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadParams() }
    LaunchedEffect(searchQuery) { resetPagination() }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Parametros Default",
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
       /* floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingParam = null
                    description = ""
                    currentValue = ""
                    previousValue = ""
                    notes = ""
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = lightTextColor)
            }
        }*/
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
                placeholder = { Text("Buscar parámetros...", color = Color.White.copy(alpha = 0.6f)) },
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
                if (isLoading && displayedParams.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(displayedParams) { param ->
                        DefaultParameterItem(
                            param = param,
                            onEditClick = {
                                editingParam = param
                                description = param.description
                                currentValue = param.currentValue.toString()
                                previousValue = param.previousValue.toString()
                                notes = param.notes ?: ""
                                showDialog = true
                            },
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor
                        )
                    }

                    if (showLoadMoreButton) {
                        item {
                            OutlinedButton(
                                onClick = { loadMoreItems() },
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
                        if (editingParam == null) "Registrar Parámetro" else "Editar Parámetro",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF4A3DAD),
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column {
                        if (editingParam == null) {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = previousValue,
                                onValueChange = { previousValue = it },
                                label = { Text("Valor Anterior") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = currentValue,
                            onValueChange = { currentValue = it },
                            label = { Text("Valor Actual") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notas (opcional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveParam() },
                        enabled = description.isNotBlank() || editingParam != null,
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
fun DefaultParameterItem(
    param: DefaultParameterResponse,
    onEditClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        param.description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Actual: ${param.currentValue}",
                            color = secondaryColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Text(
                            "Anterior: ${param.previousValue}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(secondaryColor.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = secondaryColor
                    )
                }
            }

            if (!param.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    param.notes,
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

