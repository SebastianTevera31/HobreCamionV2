package com.rfz.appflotal.presentation.ui.registrousuario.screen

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun TerminosScreen(
    modifier: Modifier = Modifier,
    @StringRes buttonText: Int = R.string.registrarse,
    context: Context,
    onBack: () -> Unit,
    onGranted: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val scrollState = rememberScrollState()
    var checked by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(Color.White)
            .padding(28.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            stringResource(R.string.terminos_condiciones),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = stringResource(R.string.main_term_text)
        )

        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = stringResource(R.string.body_terms_text))

        Spacer(modifier = Modifier.padding(16.dp))
        LinkText(
            text = stringResource(R.string.politicas_de_privacidad),
            url = "https://www.flotal.com.mx/aviso-de-privacidad",
            context = context
        )

        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
            Text(
                stringResource(R.string.aceptar_terminos_condiciones)
            )
        }

        Spacer(modifier = Modifier.padding(12.dp))
        Button(
            onClick = onGranted,
            enabled = checked,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)
        ) { Text(text = stringResource(buttonText)) }
    }
}

@Composable
fun LinkText(text: String, url: String, context: Context) {
    val terminosTitle = stringResource(R.string.terminos_condiciones)

    // Texto con estilo y anotación para el link
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        append(stringResource(R.string.invitacion_terminos))
        append(" ")
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(
            style = SpanStyle(
                color = Color(0xFF1E88E5),
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp
            )
        ) {
            append(text)
        }

        append(stringResource(R.string.asi_como_nuestros))
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://www.flotal.com.mx/terminos-y-condiciones/"
        )
        withStyle(
            style = SpanStyle(
                color = Color(0xFF1E88E5),
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp
            )
        ) {
            append(terminosTitle)
        }
        pop()
    }

    ClickableText(
        text = annotatedLinkString,
        style = MaterialTheme.typography.bodyLarge,
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

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun TermsAndConditionsPreview() {
    HombreCamionTheme {
        val context = LocalContext.current
        TerminosScreen(modifier = Modifier, context = context, onBack = {}, onGranted = {})
    }
}
