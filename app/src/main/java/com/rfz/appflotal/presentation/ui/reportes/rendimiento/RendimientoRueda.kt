package com.rfz.appflotal.presentation.ui.reportes.rendimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.TireInfoCard

@Composable
fun RendimientoRuedaView(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SimpleTopBar(
                title = "Rendimiento",
                onBack = {},
                showBackButton = true,
                subTitle = ""
            )
        },
        bottomBar = {
            CompleteFormButton(
                text = "Exportar",
                isValid = true
            ) {}
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.medium_dimen))
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_dimen))
        ) {
            Text(
                text = "Rueda P1", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            TireInfoCard(
                tire = Tire(
                    id = 202,
                    description = "Double Coin Rt500 215/75R17.5",
                    size = "215/75R17.5",
                    brand = "Double Coin",
                    model = "Rt500",
                    thread = 16.00,
                    loadingCapacity = "135/133J", // índice de carga / velocidad
                    destination = "Camión ligero",
                ),
                modifier = Modifier
                    .width(240.dp)
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Column {
                Spacer(
                    modifier = Modifier.height(
                        dimensionResource(R.dimen.small_dimen)
                    )
                )
                Text(
                    text = "Resumen",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.small_dimen)
                    )
                ) {
                    Row {
                        DataComponent(
                            title = "Odometro",
                            value = "2900 km",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        DataComponent(
                            title = "Profunidad final",
                            value = "9 mm",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                    Row {
                        DataComponent(
                            title = "Distancia recorrida actual",
                            value = "2900 km",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        DataComponent(
                            title = "Desgaste total",
                            value = "7 mm",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                    Row {
                        DataComponent(
                            title = "Ciclo actual",
                            value = "0",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }

            Column {
                HorizontalDivider(thickness = dimensionResource(R.dimen.thin_dimen))
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.small_dimen)))
                Text(
                    text = "Promedios de Desgaste",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    DataComponent(
                        title = "Por Distancia",
                        value = "414.29 mm",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    DataComponent(
                        title = "Por Ciclo",
                        value = "414.29 mm",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            Column {
                HorizontalDivider(thickness = dimensionResource(R.dimen.thin_dimen))
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.small_dimen)))
                Text(
                    text = "Costos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_dimen))) {
                    Row {
                        DataComponent(
                            title = "Costo Unitario",
                            value = "$2,000.00",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        DataComponent(
                            title = "Por Distancia",
                            value = "$0.68",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                    Row {
                        DataComponent(
                            title = "Por Profundidad",
                            value = "$285.71",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DataComponent(title: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = value, style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RendimientoRuedaViewPreview() {
    HombreCamionTheme {
        RendimientoRuedaView()
    }
}