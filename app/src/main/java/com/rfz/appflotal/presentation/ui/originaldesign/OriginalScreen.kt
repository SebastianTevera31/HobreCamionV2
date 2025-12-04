package com.rfz.appflotal.presentation.ui.originaldesign

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.rfz.appflotal.domain.brand.BrandListUseCase
import com.rfz.appflotal.domain.originaldesign.CrudOriginalDesignUseCase
import com.rfz.appflotal.domain.originaldesign.OriginalDesignUseCase
import com.rfz.appflotal.domain.utilization.UtilizationUseCase
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

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
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R

import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.domain.originaldesign.OriginalDesignByIdUseCase
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OriginalScreen(
    navController: NavController,
    originalDesignUseCase: OriginalDesignUseCase,
    originalDesignByIdUseCase: OriginalDesignByIdUseCase, // Add this
    crudOriginalDesignUseCase: CrudOriginalDesignUseCase,
    brandUseCase: BrandListUseCase,
    utilizationUseCase: UtilizationUseCase,
    homeViewModel: HomeViewModel
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = Color(0xFFF8F7FF)
    val textColor = Color(0xFF333333)
    val lightTextColor = Color.White

    val uiState by homeViewModel.uiState.collectAsState()

    val userData = uiState.userData

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current


    var allOriginalDesigns by remember { mutableStateOf<List<OriginalDesignResponse>>(emptyList()) }
    var displayedOriginalDesigns by remember {
        mutableStateOf<List<OriginalDesignResponse>>(
            emptyList()
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }


    var showDialog by remember { mutableStateOf(false) }
    var editingDesign by remember { mutableStateOf<OriginalDesignResponse?>(null) }
    var isLoadingDesignDetails by remember { mutableStateOf(false) }


    var showBrandMenu by remember { mutableStateOf(false) }
    var showUtilizationMenu by remember { mutableStateOf(false) }
    val brands = remember { mutableStateListOf<BranListResponse>() }
    val utilizations = remember { mutableStateListOf<UtilizationResponse>() }
    var isLoadingCombos by remember { mutableStateOf(false) }


    var model by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf<BranListResponse?>(null) }
    var selectedUtilization by remember { mutableStateOf<UtilizationResponse?>(null) }
    var notes by remember { mutableStateOf("") }


    fun applyFilter() {
        displayedOriginalDesigns = if (searchQuery.isBlank()) {
            allOriginalDesigns
        } else {
            allOriginalDesigns.filter { design ->
                design.model.contains(searchQuery, ignoreCase = true) ||
                        design.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun loadOriginalDesigns() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = originalDesignUseCase("Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    allOriginalDesigns = result.getOrNull() ?: emptyList()
                    displayedOriginalDesigns = allOriginalDesigns
                } else {
                    errorMessage =
                        result.exceptionOrNull()?.message
                            ?: context.getString(R.string.error_cargar_disenos_originales)
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: context.getString(R.string.error_desconocido)
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


                brands.clear()
                utilizations.clear()


                val brandsResult = brandUseCase(bearerToken, userData?.idUser!!)
                if (brandsResult.isSuccess) {
                    brands.addAll(brandsResult.getOrNull() ?: emptyList())
                }


                val utilizationsResult = utilizationUseCase()
                if (utilizationsResult.isSuccess) {
                    utilizations.addAll(utilizationsResult.getOrNull() ?: emptyList())
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


    fun loadDesignDetails(designId: Int) {
        scope.launch {
            isLoadingDesignDetails = true
            try {
                val result =
                    originalDesignByIdUseCase(designId, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    val design = result.getOrNull()?.firstOrNull()
                    design?.let {
                        model = it.fld_model
                        description = it.fld_description
                        selectedBrand = brands.find { brand -> brand.idBrand == it.c_brands_fk_1 }
                        selectedUtilization =
                            utilizations.find { util -> util.id_utilization == it.c_utilization_fk_2 }
                        notes = it.fld_notes
                    }
                } else {
                    errorMessage =
                        result.exceptionOrNull()?.message
                            ?: context.getString(R.string.error_loading_design_details)
                }
            } catch (e: Exception) {
                errorMessage = context.getString(R.string.error_loading_design_details)
            } finally {
                isLoadingDesignDetails = false
            }
        }
    }


    fun saveDesign() {
        scope.launch {
            if (model.isBlank() || description.isBlank() || selectedBrand == null || selectedUtilization == null) {
                errorMessage = context.getString(R.string.error_deben_completarse_campos)
                return@launch
            }

            try {
                val request = CrudOriginalDesignDto(
                    id_originalDesign = editingDesign?.idOriginalDesign ?: 0,
                    fld_model = model,
                    fld_description = description,
                    c_brands_fk_1 = selectedBrand!!.idBrand,
                    c_utilization_fk_2 = selectedUtilization!!.id_utilization,
                    fld_notes = notes ?: "",
                )

                val result =
                    crudOriginalDesignUseCase(request, "Bearer ${userData?.fld_token}" ?: "")
                if (result.isSuccess) {
                    showDialog = false
                    loadOriginalDesigns()
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.diseno_guardado_exitosamente),
                        duration = SnackbarDuration.Short
                    )
                } else {
                    errorMessage = context.getString(R.string.error_al_guardar_el_diseno)
                }
            } catch (e: Exception) {
                errorMessage = context.getString(R.string.error_al_guardar_el_diseno)
            }
        }
    }


    LaunchedEffect(Unit) {
        loadOriginalDesigns()
    }

    LaunchedEffect(searchQuery) {
        applyFilter()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            errorMessage = null
        }
    }


    LaunchedEffect(showDialog) {
        if (showDialog) {
            loadComboData { success ->
                if (success && editingDesign != null) {
                    loadDesignDetails(editingDesign!!.idOriginalDesign)
                } else {

                    model = ""
                    description = ""
                    selectedBrand = null
                    selectedUtilization = null
                    notes = ""
                }
            }
        } else {

            editingDesign = null
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.original_design),
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
                    editingDesign = null
                    showDialog = true
                },
                containerColor = primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Design", tint = lightTextColor)
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
                            stringResource(R.string.buscar_disenos),
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
                    isLoading && displayedOriginalDesigns.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    displayedOriginalDesigns.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isBlank()) stringResource(R.string.no_hay_disenos_registrados) else stringResource(
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
                            items(displayedOriginalDesigns) { design ->
                                OriginalDesignItem(
                                    design = design,
                                    onEditClick = {
                                        editingDesign = design
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
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    )
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = if (editingDesign == null) stringResource(R.string.registrar_diseno_original) else stringResource(
                                    R.string.editar_diseno_original
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
                    LocalizedApp {
                        if (isLoadingCombos || (editingDesign != null && isLoadingDesignDetails)) {
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.single_modelo),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = model,
                                        singleLine = true,
                                        onValueChange = { model = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                }


                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.descripcion),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = description,
                                        singleLine = true,
                                        onValueChange = { description = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            stringResource(R.string.marca),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = primaryColor,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = showBrandMenu,
                                            onExpandedChange = { showBrandMenu = !showBrandMenu }
                                        ) {
                                            OutlinedTextField(
                                                value = selectedBrand?.description ?: "",
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = showBrandMenu
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
                                                expanded = showBrandMenu,
                                                onDismissRequest = { showBrandMenu = false }
                                            ) {
                                                brands.forEach { brand ->
                                                    DropdownMenuItem(
                                                        text = { Text(brand.description) },
                                                        onClick = {
                                                            selectedBrand = brand
                                                            showBrandMenu = false
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
                                            stringResource(R.string.utilizacion),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = primaryColor,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = showUtilizationMenu,
                                            onExpandedChange = {
                                                showUtilizationMenu = !showUtilizationMenu
                                            }
                                        ) {
                                            OutlinedTextField(
                                                value = selectedUtilization?.fld_description ?: "",
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = showUtilizationMenu
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
                                                expanded = showUtilizationMenu,
                                                onDismissRequest = { showUtilizationMenu = false }
                                            ) {
                                                utilizations.forEach { utilization ->
                                                    DropdownMenuItem(
                                                        text = { Text(utilization.fld_description) },
                                                        onClick = {
                                                            selectedUtilization = utilization
                                                            showUtilizationMenu = false
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
                                        stringResource(R.string.notas),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = primaryColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    LocalizedApp {
                        Button(
                            onClick = { saveDesign() },
                            enabled = !isLoadingCombos && !isLoadingDesignDetails &&
                                    model.isNotBlank() &&
                                    description.isNotBlank() &&
                                    selectedBrand != null &&
                                    selectedUtilization != null,
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
fun OriginalDesignItem(
    design: OriginalDesignResponse,
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
                text = design.model.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = design.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${stringResource(R.string.marca)}: ${design.brandId} | ${stringResource(R.string.utilizacion)}: ${design.fld_utilization}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
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