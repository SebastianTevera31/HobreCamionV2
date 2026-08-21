package com.rfz.appflotal.presentation.ui.tiremanagment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tiremanagement.TireManagementItem
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.tiremanagment.viewmodel.TireManagementUiState
import com.rfz.appflotal.presentation.ui.tiremanagment.viewmodel.TireManagementViewModel

@Composable
fun TireManagementRoute(
    modifier: Modifier = Modifier,
    viewModel: TireManagementViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    val editClick = {

    }

    TireManagementScreen(uiState = uiState.value, onEditClick = editClick, modifier = modifier)
}

@Composable
fun TireManagementScreen(
    uiState: TireManagementUiState,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {}, modifier = modifier) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(Dimens.PaddingMedium)
        ) {
            Text(
                text = "Tire",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.items) { item ->
                    TireManagementCard(item = item, onEditClick = onEditClick)
                }
            }
        }
    }
}

@Composable
fun TireManagementCard(
    item: TireManagementItem,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
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
                text = item.title.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.treathDepth != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.profundidad_mm,
                        item.treathDepth
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                    .clickable(onClick = onEditClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.End)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        pluralStringResource(R.plurals.editar_elemento, 1),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TireManagementScreenPreview() {
    val sampleItems = listOf(
        TireManagementItem(id = 1, title = "Premium Tire A", treathDepth = 12),
        TireManagementItem(id = 2, title = "Budget Tire B", treathDepth = 8),
        TireManagementItem(id = 3, title = "All-Terrain Tire C", treathDepth = 15)
    )
    val uiState = TireManagementUiState(
        items = sampleItems
    )
    HombreCamionTheme {
        TireManagementScreen(
            uiState = uiState,
            onEditClick = {}
        )
    }
}
