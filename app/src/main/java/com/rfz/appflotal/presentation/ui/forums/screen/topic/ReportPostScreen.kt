package com.rfz.appflotal.presentation.ui.forums.screen.topic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

data class ReportType(
    val id: Int,
    val description: String
)

val reportTypes = listOf(
    ReportType(1, "Innapropiate Content"),
    ReportType(2, "Harassment"),
    ReportType(3, "Violence"),
    ReportType(4, "Nudity"),
    ReportType(5, "Hate speech"),
    ReportType(6, "Spam")
)

@Composable
fun ReportPostScreen(
    onSendReport: (reportTypeId: Int, details: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReportTypeId by remember { mutableStateOf(reportTypes.first().id) }
    var details by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.PaddingMedium)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Text(
                text = "Reportar publicación",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Selecciona el motivo del reporte:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                items(reportTypes) { reportType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReportTypeId = reportType.id }
                            .padding(vertical = Dimens.PaddingExtraSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReportTypeId == reportType.id,
                            onClick = { selectedReportTypeId = reportType.id }
                        )
                        Text(
                            text = reportType.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = Dimens.PaddingSmall),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Detalles adicionales (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                placeholder = { Text("Describe el problema...") }
            )

            Button(
                onClick = { onSendReport(selectedReportTypeId, details) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Reporte")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportPostScreenPreview() {
    HombreCamionTheme {
        ReportPostScreen(
            onSendReport = { _, _ -> },
            modifier = Modifier.safeContentPadding()
        )
    }
}
