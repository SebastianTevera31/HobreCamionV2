package com.rfz.appflotal.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.retreatedesign.screens.DescriptionText

@Composable
fun TireListScreen(
    tires: List<Tire>,
    modifier: Modifier = Modifier,
    onSelectTire: (tire: Tire) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(tires) { tire ->
            TireCard(tire = tire, modifier = Modifier.fillMaxWidth()) {
                onSelectTire(it)
            }
        }
    }
}

@Composable
fun TireCard(tire: Tire, modifier: Modifier = Modifier, onSelectTire: (tire: Tire) -> Unit) {
    ListItemContent(
        modifier = modifier.clickable {
            onSelectTire(tire)
        },
        title = tire.description,
        isEditable = false,
        onEditClick = {},
        itemContent = {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    DescriptionText(
                        title = stringResource(R.string.single_id),
                        description = tire.id.toString()
                    )
                    DescriptionText(
                        title = stringResource(R.string.marca),
                        description = tire.brand
                    )

                    DescriptionText(
                        title = stringResource(R.string.single_modelo),
                        description = tire.model
                    )
                }
                Column {
                    DescriptionText(
                        title = stringResource(R.string.single_size),
                        description = tire.size,
                    )

                    DescriptionText(
                        title = stringResource(R.string.single_profundidad),
                        description = " ${tire.thread} mm"
                    )

                    DescriptionText(
                        title = stringResource(R.string.capacidad),
                        description = tire.loadingCapacity
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true, locale = "en")
@Composable
fun TireListScreenPreview() {
    val tires = listOf(
        Tire(
            id = 1,
            brand = "Marca 1",
            description = "Modelo 1",
            size = "Tamaño 1",
            thread = 23.0,
            model = "Micheline",
            loadingCapacity = "23",
            destination = "Renovar"
        )
    )
    HombreCamionTheme {
        TireListScreen(tires = tires) {}
    }
}
