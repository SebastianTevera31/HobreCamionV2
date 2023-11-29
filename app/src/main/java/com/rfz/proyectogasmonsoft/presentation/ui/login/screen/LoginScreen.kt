package com.rfz.proyectogasmonsoft.presentation.ui.login.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.rfz.proyectogasmonsoft.R
import com.rfz.proyectogasmonsoft.presentation.ui.login.viewmodel.LoginViewModel


@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {

    Box(
        Modifier
            .fillMaxSize()


            .background(color = Color(0xfffbfdff))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(height = 215.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = 215.dp)
                    .background(color = Color(0xff5c4ec9))
            )
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 0.dp,
                    y = (-7).dp
                )
                .fillMaxWidth()
                .requiredHeight(height = 51.dp)
        ) {


        }
        Header()
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(
                    x = (-0.5).dp,
                    y = 117.dp
                )
                .requiredWidth(width = 339.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(50.dp))
                        .background(color = Color.White)
                        .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 64.dp)
                    ) {
                        ImageLogo()
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = Color.White)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 48.dp,
                            bottom = 30.dp
                        )
                ) {
                    Bienvenida()
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                        modifier = Modifier
                            .requiredWidth(width = 307.dp)
                    ) {
                        Body(loginViewModel)
                    }
                }
            }

        }
    }


}


@Composable
fun Header() {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(

            text = stringResource(R.string.app_name),
            color = Color.White,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            ),

            )


    }


}

@Composable
fun Body(loginViewModel: LoginViewModel) {

    val usuario: String by loginViewModel.usuario.observeAsState(initial = "")
    val password: String by loginViewModel.password.observeAsState(initial = "")
    val isLoginEnabled: Boolean by loginViewModel.isLoginEnable.observeAsState(initial = false)


    Usuario(usuario) {
        loginViewModel.onLoginChanged(usuario = it, password = password)

    }



    Password(password) {
        loginViewModel.onLoginChanged(usuario = usuario, password = it)
    }

    ForgetPassword()
    LoginButton(isLoginEnabled)


}


@Composable
fun LoginButton(loginEnabled: Boolean) {
    Button(
        onClick = { },
        enabled = loginEnabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = stringResource(R.string.title_ingresar))
    }


}


@Composable
fun ForgetPassword() {

    Text(
        text = stringResource(R.string.title_forget),
        color = Color(0xff5c4ec9),
        textAlign = TextAlign.Center,
        lineHeight = 11.43.em,
        style = TextStyle(
            fontSize = 14.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Password(password: String, onTextChanged: (String) -> Unit) {
    var passwordVisibility by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.title_contraseña)) },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.textFieldColors(
            Color(R.color.gray),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ), trailingIcon = {
            val imagen = if (passwordVisibility) {
                Icons.Filled.VisibilityOff

            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {

                Icon(
                    imageVector = imagen,
                    contentDescription = stringResource(R.string.content_description_icon_password)
                )
            }
        }, visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Usuario(usuario: String, onTextChanged: (String) -> Unit) {


    TextField(
        value = usuario,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.title_user)) },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.textFieldColors(
            Color(R.color.gray),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )


}


@Composable
fun Bienvenida() {
    Text(
        text = stringResource(R.string.title_bienvenida),
        color = Color(0xff566a7f),
        textAlign = TextAlign.Center,
        lineHeight = 6.25.em,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun ImageLogo() {
    Image(

        painter = painterResource(id = R.drawable.logo),


        contentDescription = stringResource(R.string.content_description_logo),
        modifier = Modifier

            .fillMaxSize()
            .clip(shape = CircleShape)
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview()
@Composable
private fun LoginPreview() {

    LoginScreen(LoginViewModel())

}