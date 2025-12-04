package com.rfz.appflotal.presentation.ui.brand.screen

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.domain.brand.BrandCrudUseCase
import com.rfz.appflotal.domain.brand.BrandListUseCase
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcasScreen(
    navController: NavController,
    brandListUseCase: BrandListUseCase,
    homeViewModel: HomeViewModel,
    brandCrudUseCase: BrandCrudUseCase
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = Color(0xFFF8F7FF)
    val lightTextColor = Color.White

    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val userData = uiState.userData

    val scope = rememberCoroutineScope()

    var allMarcas by remember { mutableStateOf<List<BranListResponse>>(emptyList()) }
    var displayedMarcas by remember { mutableStateOf<List<BranListResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showLoadMoreButton by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var editingBrand by remember { mutableStateOf<BranListResponse?>(null) }
    var newBrandName by remember { mutableStateOf("") }

    fun applyFilterAndPagination() {
        val filtered = if (searchQuery.isBlank()) allMarcas else allMarcas.filter {
            it.description.contains(searchQuery, ignoreCase = true)
        }

        val totalItems = filtered.size
        showLoadMoreButton = currentPage * ITEMS_PER_PAGE < totalItems
        displayedMarcas = filtered.take(currentPage * ITEMS_PER_PAGE)
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
                val result =
                    brandListUseCase("Bearer ${userData?.fld_token}" ?: "", userData?.idUser!!)
                if (result.isSuccess) {
                    allMarcas = result.getOrNull() ?: emptyList()
                    resetPagination()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                        ?: context.getString(R.string.error_al_cargar_marcas)
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: context.getString(R.string.error_desconocido)
            } finally {
                isLoading = false
            }
        }
    }

    fun saveBrand() {
        scope.launch {
            val result = if (editingBrand == null) {
                brandCrudUseCase(BrandCrudDto(0, newBrandName), "Bearer ${userData?.fld_token}")
            } else {
                brandCrudUseCase(
                    BrandCrudDto(editingBrand!!.idBrand, newBrandName),
                    "Bearer ${userData?.fld_token}"
                )
            }

            if (result.isSuccess) {
                showDialog = false
                loadBrands()
            } else {
                errorMessage = result.exceptionOrNull()?.message
                    ?: context.getString(R.string.error_al_guardar)
            }
        }
    }

    LaunchedEffect(Unit) { loadBrands() }
    LaunchedEffect(searchQuery) { resetPagination() }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.marcas),
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
                    editingBrand = null
                    newBrandName = ""
                    showDialog = true
                },
                containerColor = primaryColor,
                modifier = Modifier.shadow(elevation = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.nueva_marca),
                    tint = lightTextColor,
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
                            contentDescription = stringResource(R.string.buscar),
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
                            stringResource(R.string.buscar_marcas),
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
                    .background(backgroundColor)
            ) {
                if (isLoading && displayedMarcas.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryColor,
                        strokeWidth = 3.dp
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(displayedMarcas) { marca ->
                        BrandItem(
                            marca = marca,
                            onEditClick = {
                                editingBrand = marca
                                newBrandName = marca.description
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
                                    .padding(vertical = 16.dp, horizontal = 32.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = primaryColor
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(primaryColor, secondaryColor)
                                    )
                                )
                            ) {
                                Text(
                                    stringResource(R.string.cargar_mas_marcas),
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
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    )
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (editingBrand == null) stringResource(R.string.registrar_marca) else stringResource(
                                    R.string.editar_marca
                                ),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                text = {
                    LocalizedApp {
                        Column {
                            OutlinedTextField(
                                value = newBrandName,
                                onValueChange = { newBrandName = it },
                                label = {
                                    Text(
                                        stringResource(R.string.nombre_de_la_marca),
                                        color = Color.Gray
                                    )
                                },
                                isError = newBrandName.isBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color(0xFFAAAAAA),
                                    focusedLabelColor = primaryColor,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            if (newBrandName.isBlank()) {
                                Text(
                                    stringResource(R.string.el_nombre_es_requerido),
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                },
                confirmButton = {
                    LocalizedApp {
                        Button(
                            onClick = { saveBrand() },
                            enabled = newBrandName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = Color.White
                            ),
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
fun BrandItem(
    marca: BranListResponse,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = marca.description.uppercase(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                        pluralStringResource(R.plurals.editar_elemento, 1),
                        color = secondaryColor,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}