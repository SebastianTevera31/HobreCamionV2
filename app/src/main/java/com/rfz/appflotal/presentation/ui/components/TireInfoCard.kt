package com.rfz.appflotal.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tire.Tire

@Composable
fun TireInfoCard(tire: Tire?, modifier: Modifier = Modifier) {
    if (tire != null) {

        Card(
            modifier = modifier.width(240.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.detalles_de_llanta),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    pluralStringResource(
                        R.plurals.llanta_tag,
                        1,
                        ": ${tire.id}"
                    ),
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = stringResource(
                        R.string.marca_description,
                        tire.brand
                    ),
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = stringResource(
                        R.string.modelo_description,
                        tire.description
                    ),
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = stringResource(
                        R.string.size_description,
                        tire.size
                    ),
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = stringResource(
                        R.string.profundidad_description,
                        tire.thread
                    ),
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = stringResource(
                        R.string.capacidad_description,
                        tire.loadingCapacity
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}