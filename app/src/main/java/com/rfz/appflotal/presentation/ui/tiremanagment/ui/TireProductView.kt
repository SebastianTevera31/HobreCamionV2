package com.rfz.appflotal.presentation.ui.tiremanagment.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.model.tiremanagement.TireManagementItem
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun TireProductRoute(
    list: List<TireManagementItem>,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit
) {
    TireProductScreen(list = list, modifier = modifier, onEditClick = onEditClick)
}

@Composable
fun TireProductScreen(
    list: List<TireManagementItem>,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = "Tire",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(list) { item ->
                TireManagementCard(item = item, onEditClick = onEditClick)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TireProductScreenPreview() {
    val sampleItems = listOf(
        TireManagementItem(id = 1, title = "Premium Tire A", treathDepth = 12),
        TireManagementItem(id = 2, title = "Budget Tire B", treathDepth = 8),
        TireManagementItem(id = 3, title = "All-Terrain Tire C", treathDepth = 15)
    )
    HombreCamionTheme {
        TireProductScreen(
            list = sampleItems,
            onEditClick = {}
        )
    }
}