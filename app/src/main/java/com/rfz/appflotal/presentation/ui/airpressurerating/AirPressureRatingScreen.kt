package com.rfz.appflotal.presentation.ui.airpressurerating

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
import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.dto.UpdateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.AirPressureRating
import com.rfz.appflotal.domain.airPressureRating.AirPressureRatingUseCase
import com.rfz.appflotal.domain.airPressureRating.CreateAirPressureRatingUseCase
import com.rfz.appflotal.domain.airPressureRating.UpdateAirPressureRatingUseCase
import com.rfz.appflotal.presentation.theme.backgroundColor
import com.rfz.appflotal.presentation.theme.lightTextColor
import com.rfz.appflotal.presentation.theme.primaryColor
import com.rfz.appflotal.presentation.theme.secondaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirPressureRatingScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    airPressureRatingUseCase: AirPressureRatingUseCase,
    createAirPressureRatingUseCase: CreateAirPressureRatingUseCase,
    updateAirPressureRatingUseCase: UpdateAirPressureRatingUseCase
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val userData = uiState.userData
    val scope = rememberCoroutineScope()

    var allRatings by remember { mutableStateOf<List<AirPressureRating>>(emptyList()) }
    var displayedRatings by remember { mutableStateOf<List<AirPressureRating>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingRating by remember { mutableStateOf<AirPressureRating?>(null) }
    var description by remember { mutableStateOf("") }
    var minPercentage by remember { mutableStateOf("") }
    var maxPercentage by remember { mutableStateOf("") }
    var performancePercentage by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("#FFFFFF") }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allRatings else allRatings.filter {
            it.fld_description.contains(searchQuery, ignoreCase = true)
        }
        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedRatings = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadRatings() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = airPressureRatingUseCase("Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    val ratings = result.getOrNull() ?: emptyList()
                    allRatings = ratings
                    resetPagination()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar ratings"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveRating() {
        scope.launch {
            if (editingRating == null) {
                val dto = CreateAirPressureRatingDto(
                    fld_description = description,
                    fld_minimumPercentage = minPercentage.toIntOrNull() ?: 0,
                    fld_maximumPercentage = maxPercentage.toIntOrNull() ?: 0,
                    fld_performancePercentage = performancePercentage.toIntOrNull() ?: 0
                )
                val result = createAirPressureRatingUseCase(dto, "Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    showDialog = false
                    loadRatings()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
                }
            } else {
                val dtoUpdate = UpdateAirPressureRatingDto(
                    id_airPressureRating = editingRating!!.id_airPressureRating,
                    fld_minimumPercentage = minPercentage.toIntOrNull() ?: 0,
                    fld_maximumPercentage = maxPercentage.toIntOrNull() ?: 0,
                    fld_performancePercentage = performancePercentage.toIntOrNull() ?: 0
                )
                val result = updateAirPressureRatingUseCase(dtoUpdate, "Bearer ${userData?.fld_token}")
                if (result.isSuccess) {
                    showDialog = false
                    loadRatings()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar"
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadRatings() }
    LaunchedEffect(searchQuery) { resetPagination() }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Air Pressure Rating",
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
                    editingRating = null
                    description = ""
                    minPercentage = ""
                    maxPercentage = ""
                    performancePercentage = ""
                    color = "#FFFFFF"
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
                placeholder = { Text("Buscar ratings...", color = Color.White.copy(alpha = 0.6f)) },
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
                if (isLoading && displayedRatings.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(displayedRatings) { rating ->
                        AirPressureRatingItem(
                            rating = rating,
                            onEditClick = {
                                editingRating = rating
                                description = rating.fld_description
                                minPercentage = rating.fld_minimumPercentage.toString()
                                maxPercentage = rating.fld_maximumPercentage.toString()
                                performancePercentage = rating.fld_performancePercentage.toString()
                                color = rating.fld_color
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
                        if (editingRating == null) "Registrar Rating" else "Editar Rating",
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
                            onValueChange = {
                                if (editingRating == null) description = it
                            },
                            label = { Text("Descripción") },
                            enabled = editingRating == null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.DarkGray,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray,
                                disabledPlaceholderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = minPercentage,
                            onValueChange = { minPercentage = it },
                            label = { Text("Mínimo %") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = maxPercentage,
                            onValueChange = { maxPercentage = it },
                            label = { Text("Máximo %") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = performancePercentage,
                            onValueChange = { performancePercentage = it },
                            label = { Text("Performance %") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveRating() },
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
fun AirPressureRatingItem(
    rating: AirPressureRating,
    onEditClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    val displayColor = try { Color(android.graphics.Color.parseColor(rating.fld_color)) } catch (e: Exception) { Color.Gray }

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
                        text = rating.fld_description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Min: ${rating.fld_minimumPercentage}%", color = Color.Gray, fontSize = 13.sp)
                        Text("Max: ${rating.fld_maximumPercentage}%", color = Color.Gray, fontSize = 13.sp)
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


