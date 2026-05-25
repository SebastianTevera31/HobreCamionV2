package com.rfz.appflotal.presentation.ui.forums.screen.topic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.forum.ForumComment
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CommentCard

data class ReportType(
    val id: Int,
    val descriptionRes: Int
)

@Composable
fun getReportTypes() = listOf(
    ReportType(1, R.string.forum_report_type_inappropriate),
    ReportType(2, R.string.forum_report_type_harassment),
    ReportType(3, R.string.forum_report_type_violence),
    ReportType(4, R.string.forum_report_type_nudity),
    ReportType(5, R.string.forum_report_type_hate_speech),
    ReportType(6, R.string.forum_report_type_spam)
)

@Composable
fun ReportScreen(
    comment: ForumComment,
    onSendReport: (reportTypeId: Int, details: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val reportTypes = getReportTypes()
    var selectedReportTypeId by remember { mutableIntStateOf(reportTypes.first().id) }
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

            CommentCard(
                firstInitial = comment.firstInitial,
                user = comment.title,
                content = comment.description,
                imageUrl = comment.imageUrl,
                likes = comment.likes,
                isSaved = comment.isLiked,
                onReply = {},
                onSave = {},
                onSeeMore = {},
                showOptions = false,
                secondInitial = comment.secondInitial,
                time = comment.time
            )

            Text(
                text = stringResource(R.string.forum_report_post_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(R.string.forum_report_reason_label),
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
                            text = stringResource(reportType.descriptionRes),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = Dimens.PaddingSmall),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

//                item {
//                    OutlinedTextField(
//                        value = details,
//                        onValueChange = { details = it },
//                        label = { Text(stringResource(R.string.forum_report_details_label)) },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn(min = 100.dp),
//                        placeholder = { Text(stringResource(R.string.forum_report_details_placeholder)) }
//                    )
//                }
            }

            Button(
                onClick = { onSendReport(selectedReportTypeId, details) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.forum_report_send_button))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportScreenPreview() {
    HombreCamionTheme {
        ReportScreen(
            onSendReport = { _, _ -> },
            modifier = Modifier.safeContentPadding(),
            comment = ForumComment(
                id = 1,
                title = "Juan Pérez",
                description = "Este es un comentario de prueba para la pantalla de reporte.",
                imageUrl = "",
                likes = 0,
                firstInitial = "J",
                secondInitial = "P",
                isLiked = false,
                time = "Hace 2 dias",
            )
        )
    }
}
