package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.UserInfoTopBar
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UpdateUserUiState
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
    val context = LocalContext.current
    val updateUserStaus = updateUserViewModel.updateUserStatus

    LaunchedEffect(updateUserStaus) {
        when (updateUserStaus) {
            is ApiResult.Error -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_actualizar_datos),
                    Toast.LENGTH_SHORT
                ).show()
            }

            ApiResult.Loading -> {}
            is ApiResult.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.datos_actualizados_correctamente),
                    Toast.LENGTH_SHORT
                ).show()
                updateUserViewModel.cleanUpdateUserStatus()
                navigateUp()
            }
        }
    }

    Scaffold(
        topBar = {
            UserInfoTopBar(
                text = stringResource(R.string.info_de_usuario),
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
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = dimensionResource(R.dimen.huge_dimen))
            ) {
                UpdateUserView(
                    updateUserUiState = updateUserUiState.value,
                    updateUserData = { name, password, email, country, sector ->
                        updateUserViewModel.updateUserData(
                            name = name,
                            email = email,
                            password = password,
                            country = country,
                            industry = sector,
                            context = context
                        )
                    },
                    updateVehicleData = { typeVehicle, plates ->
                        updateUserViewModel.updateVehicleData(typeVehicle, plates, context)
                    })
                Button(
                    onClick = {
                        if (updateUserStaus != ApiResult.Error()) {
                            updateUserViewModel.saveUserData()
                        }
                    },
                    enabled = updateUserUiState.value.isNewData,
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
fun UpdateUserView(
    updateUserUiState: UpdateUserUiState,
    updateUserData: (
        name: String, password: String, email: String,
        country: Pair<Int, String>?, sector: Pair<Int, String>?
    ) -> Unit,
    updateVehicleData: (vehicleType: String, plates: String) -> Unit
) {
    var screenSelected by remember { mutableStateOf(UpdateUserDataViews.Chofer) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.large_dimen)),
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
                    userData = updateUserUiState.newData,
                    countries = updateUserUiState.countries,
                    industries = updateUserUiState.industries,
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) { name, password, email, country, sector ->
                    updateUserData(name, password, email, country, sector)
                }
            }

            UpdateUserDataViews.Vehiculo -> {
                UpdateVehicleScreen(
                    title = R.string.vehiculo, userData = updateUserUiState.newData,
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) { typeVehicle, plates ->
                    updateVehicleData(typeVehicle, plates)
                }
            }
        }
    }
}