package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

object ForumLimits {
    /** Máximo de caracteres del título de un tema. */
    const val TITLE_MAX_LENGTH = 100

    /** Máximo de caracteres de la descripción de un tema y de un comentario. */
    const val TEXT_MAX_LENGTH = 500
}

/** Contador "actual/máximo" que se pone en rojo al alcanzar el límite. */
@Composable
fun CharacterCounter(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$current/$max",
        style = MaterialTheme.typography.labelSmall,
        color = if (current >= max) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        textAlign = TextAlign.End,
        modifier = modifier.fillMaxWidth()
    )
}
