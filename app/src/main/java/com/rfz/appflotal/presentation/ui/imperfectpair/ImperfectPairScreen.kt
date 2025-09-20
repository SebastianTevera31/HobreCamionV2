package com.rfz.appflotal.presentation.ui.imperfectpair


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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.imperfectpair.ImperfectPairResponse
import com.rfz.appflotal.domain.imperfectpair.ImperfectPairUseCase
import com.rfz.appflotal.domain.imperfectpair.ImperfectPairCreateUseCase
import com.rfz.appflotal.domain.imperfectpair.ImperfectPairUpdateUseCase
import com.rfz.appflotal.presentation.theme.backgroundColor
import com.rfz.appflotal.presentation.theme.lightTextColor
import com.rfz.appflotal.presentation.theme.primaryColor
import com.rfz.appflotal.presentation.theme.secondaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImperfectPairScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    imperfectPairUseCase: ImperfectPairUseCase,
    createImperfectPairUseCase: ImperfectPairCreateUseCase,
    updateImperfectPairUseCase: ImperfectPairUpdateUseCase
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val scope = rememberCoroutineScope()

    var allPairs by remember { mutableStateOf<List<ImperfectPairResponse>>(emptyList()) }
    var displayedPairs by remember { mutableStateOf<List<ImperfectPairResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingPair by remember { mutableStateOf<ImperfectPairResponse?>(null) }
    var score by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }
    var renovated by remember { mutableStateOf("") }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allPairs else allPairs.filter {
            it.score.contains(searchQuery, ignoreCase = true)
        }
        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedPairs = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadPairs() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = imperfectPairUseCase("Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    val pairs = result.getOrNull() ?: emptyList()
                    allPairs = pairs
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

    fun savePair() {
        scope.launch {
            if (editingPair == null) {
                val dto = com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairCreateRequest(
                    score = score,
                    newValue = newValue.toIntOrNull() ?: 0,
                    renovated = renovated.toIntOrNull() ?: 0
                )
                val result = createImperfectPairUseCase("Bearer ${userData?.fld_token}", dto)
                if (result.isSuccess) {
                    showDialog = false
                    loadPairs()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                }
            } else {
                val dto = com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairUpdateRequest(
                    id = editingPair!!.idClassification,
                    score = score,
                    newValue = newValue.toIntOrNull() ?: 0,
                    renovated = renovated.toIntOrNull() ?: 0
                )
                val result = updateImperfectPairUseCase("Bearer ${userData?.fld_token}", dto)
                if (result.isSuccess) {
                    showDialog = false
                    loadPairs()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar"
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadPairs() }
    LaunchedEffect(searchQuery) { resetPagination() }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Imperfect Pairs",
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
                    editingPair = null
                    score = ""
                    newValue = ""
                    renovated = ""
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = lightTextColor)
            }
        }
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
                placeholder = { Text("Buscar por score...", color = Color.White.copy(alpha = 0.6f)) },
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
                if (isLoading && displayedPairs.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(displayedPairs) { pair ->
                        ImperfectPairItem(
                            pair = pair,
                            onEditClick = {
                                editingPair = pair
                                score = pair.score
                                newValue = pair.newValue.toString()
                                renovated = pair.renovated.toString()
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
                        if (editingPair == null) "Registrar Imperfect Pair" else "Editar Imperfect Pair",
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
                            value = score,
                            onValueChange = { if (editingPair == null) score = it },
                            label = { Text("Score") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = editingPair == null,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.DarkGray,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledPlaceholderColor = Color.LightGray
                            )
                        )


                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newValue = it },
                            label = { Text("New") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = renovated,
                            onValueChange = { renovated = it },
                            label = { Text("Renovated") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { savePair() },
                        enabled = score.isNotBlank(),
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
fun ImperfectPairItem(
    pair: ImperfectPairResponse,
    onEditClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
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
                        text = pair.score,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("New: ${pair.newValue}", color = Color.Gray, fontSize = 13.sp)
                        Text("Renovated: ${pair.renovated}", color = secondaryColor, fontSize = 13.sp)
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
