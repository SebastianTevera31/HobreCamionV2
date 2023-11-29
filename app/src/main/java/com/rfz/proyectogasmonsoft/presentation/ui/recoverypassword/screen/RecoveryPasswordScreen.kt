package com.rfz.proyectogasmonsoft.presentation.ui.recoverypassword.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.rfz.proyectogasmonsoft.R

@Composable
fun RecoveryPasswordScreen() {
    Box(
        Modifier
            .fillMaxSize()

            .background(color = Color.White)
    ) {
        FooterRecover()
        BodyRecover()

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
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = stringResource(R.string.content_description_back_arrow),
                    colorFilter = ColorFilter.tint(Color(0xff1e232c)),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .requiredSize(24.dp)
                )
            }
        }

    }
}

@Composable
fun BodyRecover() {
    Box(
        modifier = Modifier

            .offset(
                x = 21.1199951171875.dp,
                y = 460.25634765625.dp
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


        ButtonSendCode()
    }
    Box(
        modifier = Modifier

            .offset(
                x = 22.dp,
                y = 364.dp
            )
            .requiredWidth(width = 316.dp)
            .requiredHeight(height = 56.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(width = 316.dp)
                .requiredHeight(height = 56.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xfff7f8f9))
                .border(
                    border = BorderStroke(1.dp, Color(0xffe8ecf4)),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        EnterEmail()
    }
    Box(
        modifier = Modifier

            .offset(
                x = 22.dp,
                y = 125.dp
            )
            .fillMaxWidth()
            .requiredHeight(height = 204.dp)
    ) {
        Text(
            text = stringResource(R.string.title_forget),
            color = Color(0xff566a7f),
            lineHeight = 1.25.em,
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.title_recover),
            color = Color(0xff8391a1),
            lineHeight = 1.38.em,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 3.dp,
                    y = 132.dp
                )
                .requiredWidth(width = 313.dp)
        )
    }
}

@Composable
fun ButtonSendCode() {
    Text(
        text = stringResource(R.string.title_sendcode),
        color = Color.White,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 15.sp
        ),
        modifier = Modifier

            .offset(
                x = 119.8800048828125.dp,
                y = 16.74365234375.dp
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterEmail(){

    TextField(
        value = "",
        onValueChange = {  },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.title_enter_email)) },
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
fun FooterRecover() {
    Text(
        textAlign = TextAlign.Center,
        lineHeight = 9.sp,
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xff1e232c),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            ) { append(stringResource(R.string.title_rembember_password)) }
            withStyle(
                style = SpanStyle(
                    color = Color(0xff5c4ec9),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            ) { append(stringResource(R.string.title_ingresar)) }
        },
        modifier = Modifier

            .offset(
                x = 67.dp,
                y = 765.dp
            )
    )
}

@Preview
@Composable
fun RecoveryPasswordPreview() {
RecoveryPasswordScreen()
}