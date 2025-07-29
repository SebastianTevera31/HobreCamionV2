package com.rfz.appflotal.presentation.ui.password.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

import com.rfz.appflotal.presentation.ui.password.viewmodel.PasswordViewModel
import com.rfz.appflotal.R

@Composable
fun PasswordScreen(passwordViewModel: PasswordViewModel) {
    Box(
        Modifier
            .fillMaxSize()

            .background(color = Color.White)
    ) {

        BodyPasswordScreen(passwordViewModel)

        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 22.dp,
                    y = 125.dp
                )
                .requiredWidth(width = 338.dp)
                .requiredHeight(height = 121.dp)
        ) {
            HeaderPassword()
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 22.dp,
                    y = 56.dp
                )
                .requiredSize(size = 41.dp)
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(size = 41.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(1.dp, Color(0xffe8ecf4)),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            BackArrowPassword()
        }

    }
}

@Composable
fun BodyPasswordScreen(passwordViewModel: PasswordViewModel) {


    val password: String by passwordViewModel.password.observeAsState(initial = "")
    val repeatpassword: String by passwordViewModel.repeatpassword.observeAsState(initial = "")
    val isenablePassword: Boolean by passwordViewModel.isenablePassword.observeAsState(initial = false)

    Box(
        modifier = Modifier

            .padding(2.dp)

            .offset(
                x = 21.1199951171875.dp,
                y = 421.06396484375.dp
            )
            .requiredWidth(width = 318.dp)
            .requiredHeight(height = 56.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xff5c4ec9))
        )
        PasswordButton(isenablePassword)
    }
    Box(
        modifier = Modifier

            .offset(
                x = 22.dp,
                y = 325.dp
            )
            .requiredWidth(width = 331.dp)
            .requiredHeight(height = 56.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(width = 331.dp)
                .requiredHeight(height = 56.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xfff7f8f9))
                .border(
                    border = BorderStroke(1.dp, Color(0xffe8ecf4)),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        RepeatPassword(repeatpassword)
        {
            passwordViewModel.onPasswordChanged(password, it)
        }
    }
    Box(
        modifier = Modifier

            .offset(
                x = 22.dp,
                y = 254.dp
            )
            .requiredWidth(width = 331.dp)
            .requiredHeight(height = 56.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(width = 331.dp)
                .requiredHeight(height = 56.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xfff7f8f9))
                .border(
                    border = BorderStroke(1.dp, Color(0xffe8ecf4)),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        NewPassword(password)
        {
            passwordViewModel.onPasswordChanged(it, repeatpassword)
        }
    }


}


@Composable
fun BackArrowPassword() {
    Image(
        painter = painterResource(id = R.drawable.back_arrow),
        contentDescription = stringResource(R.string.content_description_logo),
        colorFilter = ColorFilter.tint(Color(0xff1e232c)),
        modifier = Modifier

            .requiredSize(24.dp)
    )
}

@Composable
fun HeaderPassword() {
    Text(
        text = stringResource(R.string.title_newpassword),
        color = Color(0xff566a7f),
        lineHeight = 4.33.em,
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    )
    Spacer(modifier = Modifier.size(10.dp))

    Text(
        text = stringResource(R.string.title_subtitlepassword),
        color = Color(0xff8391a1),
        lineHeight = 1.38.em,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .offset(
                x = 0.dp,
                y = 49.dp
            )
            .requiredWidth(width = 324.dp)
    )
}


@Composable
fun PasswordButton(loginEnabled: Boolean) {
    Button(
        onClick = { },
        enabled = loginEnabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = stringResource(R.string.title_restablecer_password))
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPassword(password: String, onTextChanged: (String) -> Unit) {
    var newpasswordVisibility by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.title_contraseña)) },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val imagen = if (newpasswordVisibility) {
                Icons.Filled.VisibilityOff

            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { newpasswordVisibility = !newpasswordVisibility }) {

                Icon(
                    imageVector = imagen,
                    contentDescription = stringResource(R.string.content_description_icon_password)
                )
            }
        }, visualTransformation = if (newpasswordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatPassword(repeatpassword: String, onTextChanged: (String) -> Unit) {

    var repeatpasswordVisibility by remember { mutableStateOf(false) }

    TextField(
        value = repeatpassword,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.title_contraseña)) },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val imagen = if (repeatpasswordVisibility) {
                Icons.Filled.VisibilityOff

            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { repeatpasswordVisibility = !repeatpasswordVisibility }) {

                Icon(
                    imageVector = imagen,
                    contentDescription = stringResource(R.string.content_description_icon_password)
                )
            }
        }, visualTransformation = if (repeatpasswordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )


}

@Preview
@Composable
private fun Previewpassword() {

    PasswordScreen(PasswordViewModel())

}