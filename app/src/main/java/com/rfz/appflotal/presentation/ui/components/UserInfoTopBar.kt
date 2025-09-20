package com.rfz.appflotal.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.onPrimaryLight
import com.rfz.appflotal.presentation.theme.primaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoTopBar(
    showNavigateUp: Boolean,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = ""
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = text,
                color = onPrimaryLight,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (showNavigateUp) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.back_arrow),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryLight),
        modifier = modifier
    )
}