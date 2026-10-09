package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

/**
 * Etiquetas de una publicación. Con [maxVisible] se muestran solo las primeras y un
 * indicador "+N" con las restantes (útil en tarjetas de listado).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagChips(
    tags: List<String>,
    modifier: Modifier = Modifier,
    maxVisible: Int = Int.MAX_VALUE
) {
    if (tags.isEmpty()) return

    val visible = tags.take(maxVisible)
    val hidden = tags.size - visible.size

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        visible.forEach { tag -> TagChip(text = tag) }
        if (hidden > 0) TagChip(text = "+$hidden", highlighted = false)
    }
}

@Composable
private fun TagChip(text: String, highlighted: Boolean = true) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (highlighted) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (highlighted) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagChipsPreview() {
    HombreCamionTheme {
        TagChips(
            tags = listOf("LLANTAS", "MARCA", "BARATAS", "RUTAS", "GASOLINA"),
            maxVisible = 3
        )
    }
}
