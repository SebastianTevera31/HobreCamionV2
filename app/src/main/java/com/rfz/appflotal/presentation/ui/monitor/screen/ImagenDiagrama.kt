package com.rfz.appflotal.presentation.ui.monitor.screen

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.rfz.appflotal.core.util.Commons.getBitmapFromDrawable

/** Decodifica la imagen del diagrama una vez por recurso; el ViewModel solo indica cuál es. */
@Composable
fun rememberImagenDiagrama(@DrawableRes imagen: Int?): Bitmap? {
    val context = LocalContext.current
    return remember(imagen) { imagen?.let { getBitmapFromDrawable(it, context) } }
}
