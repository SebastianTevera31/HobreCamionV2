package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.UserInfoTopBar

enum class UpdateUserDataViews() {
    Usuario,
    Vehiculo
}

@Composable
fun UpdateUserScreen(
    countries: Map<Int, String>,
    sectors: Map<Int, String>,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    updateUserData: () -> Unit
) {
    var screenSelected by remember { mutableStateOf(UpdateUserDataViews.Usuario) }

    Scaffold(
        topBar = {
            UserInfoTopBar(
                text = "Info. de Usuario",
                showNavigateUp = true,
                onNavigateUp = {
                    navigateUp()
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TabRow(
                    selectedTabIndex = when (screenSelected) {
                        UpdateUserDataViews.Usuario -> 0
                        UpdateUserDataViews.Vehiculo -> 1
                    },
                    contentColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier,
                ) {
                    Tab(
                        selected = screenSelected == UpdateUserDataViews.Usuario,
                        onClick = { screenSelected = UpdateUserDataViews.Usuario },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(R.string.title_user))
                    }
                    Tab(
                        selected = screenSelected == UpdateUserDataViews.Vehiculo,
                        onClick = { screenSelected = UpdateUserDataViews.Vehiculo },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(R.string.vehiculo))
                    }
                }

                when (screenSelected) {
                    UpdateUserDataViews.Usuario -> {}

                    UpdateUserDataViews.Vehiculo -> {}
                }


                Button(
                    onClick = updateUserData,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(160.dp)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UpdateUserDataPreview() {
    HombreCamionTheme {
        UpdateUserScreen(
            countries = mapOf(1 to "Mexico", 2 to "USA"),
            sectors = mapOf(),
            navigateUp = {}
        ) {}
    }
}