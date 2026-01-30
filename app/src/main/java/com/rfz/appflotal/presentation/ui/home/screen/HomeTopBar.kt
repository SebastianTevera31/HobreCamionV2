package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.home.utils.primaryColor
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    uiState: HomeUiState,
    languages: List<Pair<String, String>>,
    onLanguageSelected: (String) -> Unit,
    onLogout: () -> Unit,
    onShare: () -> Unit,
    onProfile: () -> Unit
) {
    TopAppBar(
        title = { LogoHeader() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = primaryColor,
            titleContentColor = Color.White
        ),
        actions = {
            LanguageSelector(
                languages = languages,
                selected = uiState.selectedLanguage,
                onSelected = onLanguageSelected
            )
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = stringResource(R.string.logout),
                    tint = Color.White
                )
            }
            IconButton(onClick = onShare) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share_feedback),
                    tint = Color.White
                )
            }
            IconButton(onClick = onProfile) {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.profile),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun LogoHeader() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.logo_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun LanguageSelector(
    languages: List<Pair<String, String>>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        languages.forEach { (code, label) ->
            Text(
                text = label,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelected(code) }
                    .background(
                        if (selected == code)
                            Color.White.copy(alpha = 0.3f)
                        else Color.Transparent
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                color = Color.White,
                fontWeight = if (selected == code) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeTopBarPreview() {
    HombreCamionTheme {
        HomeTopBar(
            uiState = HomeUiState(selectedLanguage = "es"),
            languages = emptyList(),
            onLanguageSelected = {},
            onLogout = {},
            onShare = {}
        ) { }
    }
}

