package com.rfz.appflotal.presentation.ui.reportes.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePdfReportBottomSheet(
    show: Boolean,
    tirePosition: String,
    pdfUri: Uri?,
    onDismiss: () -> Unit,
    onSharePdf: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Reporte de rendimiento"
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    if (!show) return

    ModalBottomSheet(
        modifier = modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {
        SharePdfReportContent(
            tirePosition = tirePosition,
            pdfUri = pdfUri,
            onDismiss = onDismiss,
            onSharePdf = onSharePdf,
            title = title
        )
    }
}

@Composable
fun SharePdfReportContent(
    tirePosition: String,
    pdfUri: Uri?,
    onDismiss: () -> Unit,
    onSharePdf: (Uri) -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    val isReady = pdfUri != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Rueda $tirePosition",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isReady) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "PDF",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = if (isReady) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isReady) {
                        "El reporte PDF se generó correctamente."
                    } else {
                        "No se pudo generar el reporte PDF."
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isReady) {
                        "Puedes compartirlo por WhatsApp, correo, Drive u otra aplicación."
                    } else {
                        "Verifica la configuración de FileProvider o intenta generarlo nuevamente."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = isReady,
            onClick = {
                pdfUri?.let { uri ->
                    onSharePdf(uri)
                }
            }
        ) {
            Text("Compartir PDF")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDismiss
        ) {
            Text("Cerrar")
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SharePdfReportContentPreview() {
    HombreCamionTheme {
        Surface {
            SharePdfReportContent(
                tirePosition = "Delantera Izquierda",
                pdfUri = null,
                onDismiss = {},
                onSharePdf = {},
                title = "Reporte de rendimiento"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharePdfReportContentReadyPreview() {
    HombreCamionTheme {
        Surface {
            SharePdfReportContent(
                tirePosition = "Trasera Derecha",
                pdfUri = Uri.EMPTY,
                onDismiss = {},
                onSharePdf = {},
                title = "Reporte de rendimiento"
            )
        }
    }
}
