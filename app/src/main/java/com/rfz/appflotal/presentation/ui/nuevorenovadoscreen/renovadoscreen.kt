package com.rfz.appflotal.presentation.ui.nuevorenovadoscreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenovadosScreen(navController: NavController) {
    var disenios by remember { mutableStateOf(listOf("Diseño A", "Diseño B")) }
    var showDialog by remember { mutableStateOf(false) }
    var newDiseno by remember { mutableStateOf("") }
    var newProfundidad by remember { mutableStateOf("") }
    var newDescripcion by remember { mutableStateOf("") }
    var newUtilizacion by remember { mutableStateOf("") }
    var newMarca by remember { mutableStateOf("") }
    var newApuntes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E3192),
                    titleContentColor = Color.White
                ),
                title = { Text("Diseño de Renovados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true },containerColor =Color(0xFF2E3192)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(disenios) { diseno ->
                    Text(text = diseno, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Agregar Diseño de Renovado") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDiseno,
                        onValueChange = { newDiseno = it },
                        label = { Text("Diseño") }
                    )
                    OutlinedTextField(
                        value = newProfundidad,
                        onValueChange = { newProfundidad = it },
                        label = { Text("Profundidad de Piso") }
                    )
                    OutlinedTextField(
                        value = newDescripcion,
                        onValueChange = { newDescripcion = it },
                        label = { Text("Descripción") }
                    )
                    OutlinedTextField(
                        value = newUtilizacion,
                        onValueChange = { newUtilizacion = it },
                        label = { Text("Utilización") }
                    )
                    OutlinedTextField(
                        value = newMarca,
                        onValueChange = { newMarca = it },
                        label = { Text("Marca de Renovado") }
                    )
                    OutlinedTextField(
                        value = newApuntes,
                        onValueChange = { newApuntes = it },
                        label = { Text("Apuntes") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    disenios = disenios + "$newDiseno, $newProfundidad, $newDescripcion, $newUtilizacion, $newMarca, $newApuntes"
                    showDialog = false
                    newDiseno = ""
                    newProfundidad = ""
                    newDescripcion = ""
                    newUtilizacion = ""
                    newMarca = ""
                    newApuntes = ""
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}