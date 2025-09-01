package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.UserInfoTopBar
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UpdateUserViewModel

enum class UpdateUserDataViews() {
    Chofer,
    Vehiculo
}

@Composable
fun UpdateUserScreen(
    updateUserViewModel: UpdateUserViewModel,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
) {
    val updateUserUiState = updateUserViewModel.updateUserUiState.collectAsState()

    var screenSelected by remember { mutableStateOf(UpdateUserDataViews.Chofer) }

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
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.large_dimen)),
                modifier = Modifier.fillMaxSize()
            ) {
                TabRow(
                    selectedTabIndex = when (screenSelected) {
                        UpdateUserDataViews.Chofer -> 0
                        UpdateUserDataViews.Vehiculo -> 1
                    },
                    contentColor = secondaryLight,
                    containerColor = Color.White,
                    modifier = Modifier,
                ) {
                    Tab(
                        selected = screenSelected == UpdateUserDataViews.Chofer,
                        onClick = { screenSelected = UpdateUserDataViews.Chofer },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(R.string.chofer))
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
                    UpdateUserDataViews.Chofer -> {
                        UpdateDriverScreen(
                            title = R.string.chofer,
                            userData = updateUserUiState.value.newData,
                            countries = updateUserUiState.value.countries,
                            industries = updateUserUiState.value.industries,
                            modifier = Modifier.padding(horizontal = 40.dp)
                        ) { name, password, email, country, sector ->
                            updateUserViewModel.updateUserData(
                                name = name,
                                username = password,
                                email = email,
                                password = password,
                                country = country,
                                industry = sector
                            )
                        }
                    }

                    UpdateUserDataViews.Vehiculo -> {
                        UpdateVehicleScreen(
                            title = R.string.vehiculo, userData = updateUserUiState.value.newData,
                            modifier = Modifier.padding(horizontal = 40.dp)
                        ) { typeVehicle, plates ->
                            updateUserViewModel.updateVehicleData(typeVehicle, plates)
                        }
                    }
                }

                Button(
                    onClick = { updateUserViewModel.saveUserData() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(160.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}