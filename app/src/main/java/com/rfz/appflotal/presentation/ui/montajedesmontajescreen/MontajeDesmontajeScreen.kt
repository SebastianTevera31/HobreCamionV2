package com.rfz.appflotal.presentation.ui.montajedesmontajescreen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rfz.appflotal.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MontajeDesmontajeScreen(navController: NavController) {
    var selectedCabina by remember { mutableStateOf("Cabina1") }
    var selectedTab by remember { mutableStateOf("Diagrama") }
    var selectedSubTab by remember { mutableStateOf("Profundidad") }
    var selectedLlanta by remember { mutableStateOf("") }


    var showMontajeDialog by remember { mutableStateOf(false) }
    var showDesmontajeDialog by remember { mutableStateOf(false) }
    var showInspeccionDialog by remember { mutableStateOf(false) }


    var folio by remember { mutableStateOf("") }
    var profPiso by remember { mutableStateOf("") }
    var presionInspeccionada by remember { mutableStateOf("") }
    var montadoPor by remember { mutableStateOf("") }
    var reporte by remember { mutableStateOf("") }
    var base by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var inspeccionadoPor by remember { mutableStateOf("") }
    var profPiso1 by remember { mutableStateOf("") }
    var profPiso2 by remember { mutableStateOf("") }
    var profPiso3 by remember { mutableStateOf("") }
    var profPiso4 by remember { mutableStateOf("") }
    var informeInspeccion by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2E3192),
                titleContentColor = Color.White
            ),title = { Text("Montaje y Desmontaje") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { innerPadding ->
       /* Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
        {

            CabinaDropdownMenu(selectedCabina) { selectedCabina = it }


            TabRow(selectedTabIndex = if (selectedTab == "Diagrama") 0 else 1,
                containerColor = Color(0xFF3F51B5), contentColor = Color.White,
                ) {
                Tab(selected = selectedTab == "Diagrama", onClick = { selectedTab = "Diagrama" }) {
                    Text("Diagrama", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == "Posiciones", onClick = { selectedTab = "Posiciones" }) {
                    Text("Posiciones", modifier = Modifier.padding(16.dp))
                }
            }

            when (selectedTab) {
                "Diagrama" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        VehicleDiagram(
                            onLlantaClick = { llanta ->
                                selectedLlanta = "Llantas seleccionada: $llanta"
                            }
                        )

                        Spacer(Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profundidad
                            Text(
                                "Profundidad",
                                modifier = Modifier
                                    .weight(1f)
                                    .background( Color(0xFFD0BCFF))
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )

                            // Presion
                            Text(
                                "Presión",
                                modifier = Modifier
                                    .weight(1f)
                                    .background( Color(0xFFCCC2DC))
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )

                            // Medida
                            Text(
                                "Medida",
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFFEFB8C8))
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        ) {
                            CircularIcon("Montaje", R.drawable.ic_montaje) {
                                showMontajeDialog = true
                            }
                            CircularIcon("Desmontaje", R.drawable.ic_desmontaje) {
                                showDesmontajeDialog = true
                            }
                            CircularIcon("Inspeccion", R.drawable.ic_inspeccion) {
                                showInspeccionDialog = true
                            }
                        }
                    }
                }
                "Posiciones" -> {
                    Text("Contenido para Posiciones", modifier = Modifier.padding(16.dp))
                }
            }
        }*/
    }
    // Dialogs
    if (showMontajeDialog) {
        MontajeDialog(
            folio = folio,
            montadoPor = montadoPor,
            profPiso = profPiso,
            presionInspeccionada = presionInspeccionada,
            onFolioChange = { folio = it },
            onMontadoPorChange = { montadoPor = it },
            onProfPisoChange = { profPiso = it },
            onPresionChange = { presionInspeccionada = it },
            onDismiss = { showMontajeDialog = false },
            onProcess = {

                showMontajeDialog = false
            }
        )
    }

    if (showDesmontajeDialog) {
        DesmontajeDialog(
            folio = folio,
            reporte = reporte,
            base = base,
            destino = destino,
            desmontadoPor = montadoPor,
            profPiso = profPiso,
            presionInspeccionada = presionInspeccionada,
            onFolioChange = { folio = it },
            onReporteChange = { reporte = it },
            onBaseChange = { base = it },
            onDestinoChange = { destino = it },
            onDesmontadoPorChange = { montadoPor = it },
            onProfPisoChange = { profPiso = it },
            onPresionChange = { presionInspeccionada = it },
            onDismiss = { showDesmontajeDialog = false },
            onProcess = {

                showDesmontajeDialog = false
            }
        )
    }

    if (showInspeccionDialog) {
        InspeccionDialog(
            folio = folio,
            inspeccionadoPor = inspeccionadoPor,
            profPiso1 = profPiso1,
            profPiso2 = profPiso2,
            profPiso3 = profPiso3,
            profPiso4 = profPiso4,
            presionInspeccionada = presionInspeccionada,
            presionAjustada = "",
            informeInspeccion = informeInspeccion,
            onFolioChange = { folio = it },
            onInspeccionadoPorChange = { inspeccionadoPor = it },
            onProfPiso1Change = { profPiso1 = it },
            onProfPiso2Change = { profPiso2 = it },
            onProfPiso3Change = { profPiso3 = it },
            onProfPiso4Change = { profPiso4 = it },
            onPresionInspeccionadaChange = { presionInspeccionada = it },
            onInformeChange = { informeInspeccion = it },
            onDismiss = { showInspeccionDialog = false },
            onProcess = {

                showInspeccionDialog = false
            }
        )
    }
}

@Composable
fun VehicleDiagram(onLlantaClick: (Int) -> Unit) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            LlantaIcon(number = 1, onClick = onLlantaClick)
            LlantaIcon(number = 2, onClick = onLlantaClick)
        }

        Spacer(modifier = Modifier.height(16.dp))


        Box(
            modifier = Modifier
                .size(width = 400.dp, height = 50.dp)
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                text = " Vehículo",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LlantaIcon(number = 3, onClick = onLlantaClick)
                LlantaIcon(number = 4, onClick = onLlantaClick)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LlantaIcon(number = 5, onClick = onLlantaClick)
                LlantaIcon(number = 6, onClick = onLlantaClick)
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LlantaIcon(number = 7, onClick = onLlantaClick)
                LlantaIcon(number = 8, onClick = onLlantaClick)
            }
        }
    }
}

@Composable
fun LlantaIcon(number: Int, onClick: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick(number) }
    ) {
        Image(
            painter = painterResource(id = R.drawable.llanta),
            contentDescription = "Llanta $number",
            modifier = Modifier
                .size(30.dp)
                .background(Color.LightGray, shape = CircleShape)
        )
        Text(
            text = "Llanta $number",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CabinaDropdownMenu(selectedCabina: String, onCabinaSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val cabinas = listOf("Cabina1" to 1, "Cabina2" to 2, "Cabina3" to 3)

    Box(modifier = Modifier.fillMaxWidth().padding(0.dp)) {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RectangleShape, colors = ButtonDefaults.buttonColors(

            containerColor = Color(0xFF2E3192),


            )) {
            Text("$selectedCabina")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cabinas.forEach { (name, value) ->

                DropdownMenuItem(
                    text = { Text(text = "$name ($value)") },
                    onClick = {
                        onCabinaSelected(name)

                    },

                    )
            }
        }
    }
}

@Composable
fun CircularIcon(label: String, iconRes: Int, onClick: () -> Unit) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .size(60.dp)
            .clip(CircleShape)
            .background(Color(0xFF2E3192))
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,

            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}


@Composable
fun MontajeDialog(
    folio: String,
    montadoPor: String,
    profPiso: String,
    presionInspeccionada: String,
    onFolioChange: (String) -> Unit,
    onMontadoPorChange: (String) -> Unit,
    onProfPisoChange: (String) -> Unit,
    onPresionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onProcess: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Montaje de llanta") },
        text = {
            Column {
                TextField(value = folio, onValueChange = onFolioChange, label = { Text("Folio") })
                Spacer(Modifier.height(10.dp))
                TextField(value = montadoPor, onValueChange = onMontadoPorChange, label = { Text("Montado por") })
                Spacer(Modifier.height(10.dp))
                TextField(value = profPiso, onValueChange = onProfPisoChange, label = { Text("Profundidad Piso") })
                Spacer(Modifier.height(10.dp))
                TextField(value = presionInspeccionada, onValueChange = onPresionChange, label = { Text("Presión Inspeccionada") })
            }
        },
        confirmButton = {
            Button(onClick = onProcess) {
                Text("Procesar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DesmontajeDialog(
    folio: String,
    reporte: String,
    base: String,
    destino: String,
    desmontadoPor: String,
    profPiso: String,
    presionInspeccionada: String,
    onFolioChange: (String) -> Unit,
    onReporteChange: (String) -> Unit,
    onBaseChange: (String) -> Unit,
    onDestinoChange: (String) -> Unit,
    onDesmontadoPorChange: (String) -> Unit,
    onProfPisoChange: (String) -> Unit,
    onPresionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onProcess: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Desmontaje de llanta") },
        text = {
            Column {
                TextField(value = folio, onValueChange = onFolioChange, label = { Text("Folio") })
                Spacer(Modifier.height(10.dp))
                TextField(value = reporte, onValueChange = onReporteChange, label = { Text("Reporte") })
                Spacer(Modifier.height(10.dp))
                TextField(value = base, onValueChange = onBaseChange, label = { Text("Base") })
                Spacer(Modifier.height(10.dp))
                TextField(value = destino, onValueChange = onDestinoChange, label = { Text("Destino") })
                Spacer(Modifier.height(10.dp))
                TextField(value = desmontadoPor, onValueChange = onDesmontadoPorChange, label = { Text("Desmontado por") })
                Spacer(Modifier.height(10.dp))
                TextField(value = profPiso, onValueChange = onProfPisoChange, label = { Text("Profundidad Piso") })
                Spacer(Modifier.height(10.dp))
                TextField(value = presionInspeccionada, onValueChange = onPresionChange, label = { Text("Presión Inspeccionada") })
            }
        },
        confirmButton = {
            Button(onClick = onProcess) {
                Text("Procesar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun InspeccionDialog(
    folio: String,
    inspeccionadoPor: String,
    profPiso1: String,
    profPiso2: String,
    profPiso3: String,
    profPiso4: String,
    presionInspeccionada: String,
    presionAjustada: String,
    informeInspeccion: String,
    onFolioChange: (String) -> Unit,
    onInspeccionadoPorChange: (String) -> Unit,
    onProfPiso1Change: (String) -> Unit,
    onProfPiso2Change: (String) -> Unit,
    onProfPiso3Change: (String) -> Unit,
    onProfPiso4Change: (String) -> Unit,
    onPresionInspeccionadaChange: (String) -> Unit,
    onInformeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onProcess: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Inspección de llanta") },
        text = {
            Column {
                TextField(value = folio, onValueChange = onFolioChange, label = { Text("Folio") })
                Spacer(Modifier.height(10.dp))
                TextField(value = inspeccionadoPor, onValueChange = onInspeccionadoPorChange, label = { Text("Inspeccionado por") })
                Spacer(Modifier.height(10.dp))
                TextField(value = profPiso1, onValueChange = onProfPiso1Change, label = { Text("Profundidad Piso 1") })
                Spacer(Modifier.height(10.dp))
                TextField(value = profPiso2, onValueChange = onProfPiso2Change, label = { Text("Profundidad Piso 2") })
                Spacer(Modifier.height(10.dp))
                TextField(value = profPiso3, onValueChange = onProfPiso3Change, label = { Text("Profundidad Piso 3") })
                Spacer(Modifier.height(10.dp))
                TextField(value = profPiso4, onValueChange = onProfPiso4Change, label = { Text("Profundidad Piso 4") })
                Spacer(Modifier.height(10.dp))
                TextField(value = presionInspeccionada, onValueChange = onPresionInspeccionadaChange, label = { Text("Presión Inspeccionada") })
                Spacer(Modifier.height(10.dp))
                TextField(value = presionAjustada, onValueChange = { /* manejar cambio de presion ajustada */ }, label = { Text("Presión Ajustada") })
                Spacer(Modifier.height(10.dp))
                TextField(value = informeInspeccion, onValueChange = onInformeChange, label = { Text("Informe de Inspección") })
            }
        },
        confirmButton = {
            Button(onClick = onProcess) {
                Text("Procesar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}