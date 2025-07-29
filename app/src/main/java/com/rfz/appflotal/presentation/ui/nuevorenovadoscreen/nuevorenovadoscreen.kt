package com.rfz.appflotal.presentation.ui.nuevorenovadoscreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRenovadoScreen(navController: NavController) {
    var diseño by remember { mutableStateOf("") }
    var profundidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var utilizacion by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var apuntes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextField(value = diseño, onValueChange = { diseño = it }, label = { Text("Diseño") })
        TextField(value = profundidad, onValueChange = { profundidad = it }, label = { Text("Profundidad de Piso") })
        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
        TextField(value = utilizacion, onValueChange = { utilizacion = it }, label = { Text("Utilización") })
        TextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca de Renovado") })
        TextField(value = apuntes, onValueChange = { apuntes = it }, label = { Text("Apuntes") })

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
