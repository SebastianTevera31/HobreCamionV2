package com.rfz.proyectogasmonsoft.presentation.ui.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.rfz.proyectogasmonsoft.R

@Composable
fun HomeScreen() {

    Box(
        Modifier
            .fillMaxSize()

            .background(color = Color.White)
    ) {
        HeaderHome()
        BodyHome()
    }

}

@Composable
fun BodyHome() {
    Box(
        modifier = Modifier

            .offset(
                x = 38.dp,
                y = 517.dp
            )
            .requiredWidth(width = 273.dp)
            .requiredHeight(height = 56.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(width = 273.dp)
                .requiredHeight(height = 56.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xff5c4ec9))
        )
        ButtonInicioHome()
    }
    Box(
        modifier = Modifier

            .offset(
                x = 38.dp,
                y = 726.dp
            )
            .requiredWidth(width = 273.dp)
            .requiredHeight(height = 56.dp)
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(width = 273.dp)
                .requiredHeight(height = 56.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xff5c4ec9))
        )
        ButtonFinHome()
    }
    Box(
        modifier = Modifier

            .offset(
                x = 57.dp,
                y = 245.dp
            )
            .requiredWidth(width = 229.dp)
            .requiredHeight(height = 31.dp)
    ) {
        SubTitleComienzoCarga()
    }
    ImageBodyHome()
    Box(
        modifier = Modifier

            .offset(
                x = 84.dp,
                y = 605.dp
            )
            .requiredWidth(width = 181.dp)
            .requiredHeight(height = 90.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(color = Color.White)
    ) {
        TimeHome()
    }
}

@Composable
fun TimeHome() {
    Text(
        text = "19:49",
        color = Color(0xff5c4ec9),
        textAlign = TextAlign.Center,
        lineHeight = 0.36.em,
        style = TextStyle(
            fontSize = 50.sp,
            fontWeight = FontWeight.Black
        ),
        modifier = Modifier

            .offset(
                x = 0.dp,
                y = (-0.5).dp
            )
            .fillMaxWidth()
            .requiredHeight(height = 63.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
fun ImageBodyHome() {
    Image(
        painter = painterResource(id = R.drawable.homeclose),
        contentDescription = stringResource(R.string.content_description_home_close),
        contentScale = ContentScale.Fit,
        modifier = Modifier

            .offset(
                x = 65.dp,
                y = 306.dp
            )
            .requiredWidth(width = 211.dp)
            .requiredHeight(height = 181.dp)
    )
}

@Composable
fun SubTitleComienzoCarga() {
    Text(
        text = stringResource(R.string.subtitle_home),
        color = Color(0xff566a7f),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun ButtonFinHome() {
    Text(
        text = stringResource(R.string.title_buttonfin),
        color = Color.White,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 15.sp
        ),
        modifier = Modifier

            .offset(
                x = 85.dp,
                y = 19.dp
            )
            .requiredWidth(width = 104.dp)
    )
}

@Composable
fun ButtonInicioHome() {
    Text(
        text = stringResource(R.string.title_buttoninicio),
        color = Color.White,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 15.sp
        ),
        modifier = Modifier

            .offset(
                x = 85.dp,
                y = 19.dp
            )
            .requiredWidth(width = 104.dp)
    )
}

@Composable
fun HeaderHome() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(height = 215.dp)
                .background(color = Color(0xff5c4ec9))
        )
        TitleHome()
        ImageHome()
    }

    HoraHome()
    FechaHome()
}

@Composable
fun FechaHome() {
    Text(
        text = "Lunes, Diciembre 23",
        color = Color.White,
        textAlign = TextAlign.Center,
        lineHeight = 1.2.em,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Light
        ),
        modifier = Modifier

            .offset(
                x = 6.dp,
                y = 173.dp
            )
            .requiredWidth(width = 358.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
fun HoraHome() {
    Text(
        text = "9:41",
        color = Color.White,
        textAlign = TextAlign.Center,
        lineHeight = 1.08.em,
        style = TextStyle(
            fontSize = 72.sp,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.25f),
                offset = Offset(0f, 4f),
                blurRadius = 4f
            )
        ),
        modifier = Modifier

            .offset(
                x = 6.dp,
                y = 99.dp
            )
            .requiredWidth(width = 354.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
fun ImageHome() {
    Image(
        painter = painterResource(id = R.drawable.openpane),
        contentDescription = stringResource(R.string.content_description_pane),
        contentScale = ContentScale.Fit,
        modifier = Modifier

            .offset(
                x = 292.dp,
                y = 52.dp
            )
            .requiredSize(size = 40.dp)
    )
}

@Composable
fun TitleHome() {
    Text(
        text = stringResource(R.string.app_name),
        color = Color.White,
        style = TextStyle(
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier

            .offset(
                x = 26.dp,
                y = 48.dp
            )
            .requiredWidth(width = 224.dp)
    )
}

@Preview
@Composable
fun HomePreview() {
    HomeScreen()
}