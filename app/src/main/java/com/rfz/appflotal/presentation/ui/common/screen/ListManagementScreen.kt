package com.rfz.appflotal.presentation.ui.common.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.common.viewmodel.ListManagementUiState

/**
 * Un Composable genérico y reutilizable que define la estructura para todas las pantallas
 * de tipo "Maestro-Detalle" o "Gestión de Listas".
 *
 * @param T El tipo de dato de los ítems que se muestran en la lista.
 * @param state El estado actual de la UI, proporcionado por el ViewModel.
 * @param onSearchQueryChanged Lambda para notificar cambios en la barra de búsqueda.
 * @param onShowDialog Lambda para notificar la intención de mostrar el diálogo.
 * @param listItemContent El "slot" para el contenido de un solo ítem de la lista. Cada pantalla
 *                      define aquí cómo se ve una de sus filas.
 * @param dialogContent El "slot" para el contenido del diálogo. Cada pantalla define aquí
 *                      el formulario específico para crear/editar sus ítems.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ListManagementScreen(
    state: ListManagementUiState<T>,
    onSearchQueryChanged: (String) -> Unit,
    onShowDialog: () -> Unit,
    listItemContent: @Composable (item: T) -> Unit,
    dialogContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            state.title, style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        )
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChanged,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.White.copy(alpha = 0.9f)
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Limpiar",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(16.dp)),
                        placeholder = {
                            Text(
                                "Buscar...",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.4f
                            ),
                            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.1f
                            ),
                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.1f
                            )
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowDialog, containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.shadow(elevation = 8.dp, shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = "Añadir ítem", tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->

        Box(
            modifier = Modifier

                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.items) { item ->
                        // Aquí es donde se "inyecta" el Composable de cada fila específica.
                        listItemContent(item)
                    }
                }
            }
        }

        // Aquí es donde se "inyecta" el Composable del diálogo específico.
        if (state.showDialog) {
            dialogContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListManagementScreenPreview() {
    HombreCamionTheme {
        ListManagementScreen(
            state = ListManagementUiState(
                title = "Título de la Lista",
                items = listOf(
                    "Elemento 1",
                    "Elemento 2",
                    "Elemento 3"
                ),
                searchQuery = "",
                isLoading = false,
                showDialog = false
            ),
            onSearchQueryChanged = {},
            onShowDialog = {},
            listItemContent = { data -> Text(text = data, modifier = Modifier.fillMaxSize()) },
            dialogContent = {}
        )
    }
}

