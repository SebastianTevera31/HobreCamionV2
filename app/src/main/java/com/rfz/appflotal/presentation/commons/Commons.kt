package com.rfz.appflotal.presentation.commons

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun TermsAndConditionsText(text: String, context: Context, modifier: Modifier = Modifier) {
    // Texto con estilo y anotación para el link
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://www.flotal.com.mx"
        )
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(text)
        }
        pop()
    }

    ClickableText(
        text = annotatedLinkString,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
        onClick = { offset ->
            annotatedLinkString.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
        }
    )
}