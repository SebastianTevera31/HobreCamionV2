package com.rfz.appflotal.presentation.ui.medidasllantasscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.data.model.tire.dto.TireSizeDto
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.domain.tire.TireSizeCrudUseCase
import com.rfz.appflotal.domain.tire.TireSizeUseCase
import com.rfz.appflotal.presentation.theme.backgroundLight
import com.rfz.appflotal.presentation.theme.onPrimaryContainerLight
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight

import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedidasLlantasScreen(
    navController: NavController,
    tireSizeUseCase: TireSizeUseCase,
    homeViewModel: HomeViewModel,
    tireSizeCrudUseCase: TireSizeCrudUseCase
) {


    val uiState by homeViewModel.uiState.collectAsState()


    val userData = uiState.userData


    val scope = rememberCoroutineScope()

    var allMedidas by remember { mutableStateOf<List<TireSizeResponse>>(emptyList()) }
    var displayedMedidas by remember { mutableStateOf<List<TireSizeResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingMedida by remember { mutableStateOf<TireSizeResponse?>(null) }
    var newMedida by remember { mutableStateOf("") }
    var newNota by remember { mutableStateOf("") }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allMedidas else allMedidas.filter {
            it.fld_size.contains(searchQuery, ignoreCase = true) ||
                    it.fld_notes?.contains(searchQuery, ignoreCase = true) ?: false
        }

        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedMedidas = filtered.take(currentPage * ITEMS_PER_PAGE)
    }

    fun loadMoreItems() {
        currentPage++
        applyFilterAndPagination()
    }

    fun resetPagination() {
        currentPage = 1
        applyFilterAndPagination()
    }

    fun loadMedidas() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = tireSizeUseCase.doTireSizes(
                    userData?.idUser!!,
                    "Bearer ${userData?.fld_token}" ?: ""
                )
                if (result.isSuccessful) {
                    allMedidas = result.body() ?: emptyList()
                    resetPagination()
                } else {
                    errorMessage = result.message() ?: "Error al cargar medidas de llantas"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun saveMedida() {
        scope.launch {
            val requestBody = TireSizeDto(
                id_tireSize = editingMedida?.id_tireSize ?: 0,
                fld_size = newMedida ?: "",
                fld_notes = newNota ?: "",
                c_user_fk_1 = userData?.idUser ?: 0
            )

            val result = tireSizeCrudUseCase(requestBody, "Bearer ${userData?.fld_token}")

            if (result.isSuccess) {
                showDialog = false
                loadMedidas()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error al guardar"
            }
        }
    }

    LaunchedEffect(Unit) { loadMedidas() }
    LaunchedEffect(searchQuery) { resetPagination() }

    Scaffold(
        containerColor = backgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medidas de Llantas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
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
                    editingMedida = null
                    newMedida = ""
                    newNota = ""
                    showDialog = true
                },
                containerColor = primaryLight,
                modifier = Modifier.shadow(elevation = 8.dp, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nueva Medida",
                    tint = onPrimaryContainerLight,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Barra de búsqueda elegante
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
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            "Buscar medidas...",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
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
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundLight)
            ) {
                if (isLoading && displayedMedidas.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryLight,
                        strokeWidth = 3.dp
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(displayedMedidas) { medida ->
                        MedidaItem(
                            medida = medida,
                            onEditClick = {
                                editingMedida = medida
                                newMedida = medida.fld_size
                                newNota = medida.fld_notes ?: ""
                                showDialog = true
                            },
                            primaryColor = primaryLight,
                            secondaryColor = secondaryLight
                        )
                    }

                    if (showLoadMoreButton) {
                        item {
                            OutlinedButton(
                                onClick = { loadMoreItems() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 32.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = primaryLight
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(primaryLight, secondaryLight)
                                    )
                                )
                            ) {
                                Text(
                                    "Cargar más medidas",
                                    fontWeight = FontWeight.Medium
                                )
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (editingMedida == null) "Registrar Medida" else "Editar Medida",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color(0xFF4A3DAD),
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newMedida,
                            onValueChange = { newMedida = it },
                            label = {
                                Text(
                                    "Medida de la llanta",
                                    color = Color.Gray
                                )
                            },
                            isError = newMedida.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6A5DD9),
                                unfocusedBorderColor = Color(0xFFAAAAAA),
                                focusedLabelColor = Color(0xFF6A5DD9),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        if (newMedida.isBlank()) {
                            Text(
                                "La medida es requerida",
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newNota,
                            onValueChange = { newNota = it },
                            label = {
                                Text(
                                    "Nota (opcional)",
                                    color = Color.Gray
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6A5DD9),
                                unfocusedBorderColor = Color(0xFFAAAAAA),
                                focusedLabelColor = Color(0xFF6A5DD9),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { saveMedida() },
                        enabled = newMedida.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A5DD9),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("GUARDAR", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialog = false },
                        border = BorderStroke(1.dp, Color(0xFF6A5DD9)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("CANCELAR", color = Color(0xFF6A5DD9), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun MedidaItem(
    medida: TireSizeResponse,
    onEditClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = primaryColor.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = medida.fld_size,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(secondaryColor.copy(alpha = 0.1f))
                        .clickable { onEditClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = secondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Editar",
                            color = secondaryColor,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            if (!medida.fld_notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = medida.fld_notes,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}