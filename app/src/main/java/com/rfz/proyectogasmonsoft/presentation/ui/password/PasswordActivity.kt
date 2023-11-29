package com.rfz.proyectogasmonsoft.presentation.ui.password

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.proyectogasmonsoft.presentation.theme.ProyectoGasMonSoftTheme
import com.rfz.proyectogasmonsoft.presentation.ui.login.viewmodel.LoginViewModel
import com.rfz.proyectogasmonsoft.presentation.ui.password.screen.PasswordScreen
import com.rfz.proyectogasmonsoft.presentation.ui.password.viewmodel.PasswordViewModel


class PasswordActivity : ComponentActivity() {

    private  val passwordViewModel: PasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoGasMonSoftTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    PasswordScreen(passwordViewModel)
                }
            }
        }
    }

    @Preview
    @Composable
    fun Passwordpreview(){
        PasswordScreen(passwordViewModel)
    }
}


