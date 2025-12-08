package com.rfz.appflotal.presentation.ui.home.screen

import android.graphics.Canvas
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareFeedbackScreen(
    messageOperationState: OperationStatus,
    onShare: (feedback: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var feedback by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scroll = rememberScrollState()

    when (messageOperationState) {
        OperationStatus.Error -> {
            Toast.makeText(
                context,
                stringResource(R.string.error_enviar_comentarios),
                Toast.LENGTH_SHORT
            ).show()
        }

        OperationStatus.Loading -> {}
        OperationStatus.Success -> {
            onBack()
            Toast.makeText(
                context,
                stringResource(R.string.gracias_por_su_comentario),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.share_feedback),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = { onShare(feedback) },
                enabled = feedback.trim().isNotEmpty(),
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiary,
                )
            ) { Text(stringResource(R.string.submit).uppercase()) }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_dimen)),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.large_dimen))
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            TextField(
                value = feedback,
                onValueChange = { feedback = it },
                placeholder = { Text(text = stringResource(R.string.enter_your_feedback_here)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.large_dimen))
                    .weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "en")
@Composable
fun ShareFeedbackScreenPreview() {
    HombreCamionTheme {
        ShareFeedbackScreen(
            messageOperationState = OperationStatus.Loading,
            onShare = {},
            onBack = {})
    }
}
