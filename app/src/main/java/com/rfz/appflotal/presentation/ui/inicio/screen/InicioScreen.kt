package com.rfz.appflotal.presentation.ui.inicio.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.rfz.appflotal.R


@Composable
fun InicioScreen(
    navController: NavController,


) {

    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Body(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            navController

        )
    }
}

@Composable
fun Body(
    modifier: Modifier,
    navController: NavController,

) {
    val viewModelScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ImageLogoInicio()

        Spacer(modifier = Modifier.height(22.dp))
        InicioButton(navController)
    }
}

@Composable
fun ImageLogoInicio() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = stringResource(R.string.content_description_logo),
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp))

    )
}



@SuppressLint("ResourceAsColor")
@Composable
fun InicioButton(navController: NavController) {
    Button(
        onClick = {
            try {
                navController.navigate("loginScreen")
                {
                    popUpTo("InicioScreen") { inclusive = true }
                }
            } catch (e: Exception) {

            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color(R.color.purple_500),
            disabledContentColor = Color(R.color.purple_500),
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = stringResource(R.string.title_ingresar),
            style = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
