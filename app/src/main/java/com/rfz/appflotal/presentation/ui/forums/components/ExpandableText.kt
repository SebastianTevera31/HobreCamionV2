package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rfz.appflotal.R

private const val DEFAULT_COLLAPSE_THRESHOLD = 250
private const val DEFAULT_COLLAPSED_LINES = 4

/**
 * Texto que se compacta cuando supera [collapseThreshold] caracteres (o demasiados saltos de
 * línea) y se puede expandir con un botón "Ver más" / "Ver menos".
 */
@Composable
fun ExpandableText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    collapseThreshold: Int = DEFAULT_COLLAPSE_THRESHOLD,
    collapsedMaxLines: Int = DEFAULT_COLLAPSED_LINES
) {
    val isLong = text.length > collapseThreshold || text.count { it == '\n' } >= collapsedMaxLines
    var expanded by rememberSaveable(text) { mutableStateOf(false) }

    Column(modifier = modifier.animateContentSize()) {
        Text(
            text = text,
            style = style,
            maxLines = if (isLong && !expanded) collapsedMaxLines else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
        if (isLong) {
            Text(
                text = stringResource(if (expanded) R.string.ver_menos else R.string.ver_mas),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}
