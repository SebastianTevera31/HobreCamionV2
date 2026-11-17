package com.rfz.appflotal.presentation.commons

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.ui.services.components.FieldLabel
import java.io.File

@Composable
fun ImagePickerField(
    label: String,
    editable: Boolean,
    newImage: Uri?,
    onImageSelected: (Uri) -> Unit,
    onRemoveNewImage: () -> Unit,
    modifier: Modifier = Modifier,
    currentImageUrl: String? = null,
    photoPrefix: String = "photo"
) {
    val context = LocalContext.current
    var pendingCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showViewer by rememberSaveable { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        pendingCameraUri?.takeIf { success }?.let(onImageSelected)
        pendingCameraUri = null
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onImageSelected) }

    // Si la URL falla al cargar se trata como si no hubiera imagen. Una URL nueva
    // (p. ej. tras recargar la lista) se vuelve a intentar.
    var remoteFailed by remember(currentImageUrl) { mutableStateOf(false) }
    val remoteUrl = currentImageUrl?.takeIf { it.isNotBlank() && !remoteFailed }
    val onRemoteError = {
        remoteFailed = true
        showViewer = false
    }
    if (!editable && newImage == null && remoteUrl == null) return

    if (showViewer && remoteUrl != null) {
        RemoteImageViewer(url = remoteUrl, onError = onRemoteError) { showViewer = false }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        FieldLabel(label)

        if (newImage != null || remoteUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                val previewModifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(Dimens.PaddingSmall))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                if (newImage != null) {
                    AsyncImage(
                        model = newImage,
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        modifier = previewModifier
                    )
                } else if (remoteUrl != null) {
                    RemoteImage(
                        url = remoteUrl,
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        onError = onRemoteError,
                        modifier = previewModifier.clickable { showViewer = true }
                    )
                }
                if (editable && newImage != null) {
                    IconButton(
                        onClick = onRemoveNewImage,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Dimens.PaddingSmall)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.srv_quitar_imagen),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (editable) Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
            OutlinedButton(
                onClick = {
                    val uri = createPhotoUri(context, photoPrefix)
                    pendingCameraUri = uri
                    cameraLauncher.launch(uri)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(Modifier.size(Dimens.PaddingSmall))
                Text(stringResource(R.string.srv_tomar_foto))
            }
            OutlinedButton(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.size(Dimens.PaddingSmall))
                Text(stringResource(R.string.srv_galeria))
            }
        }
    }
}

/**
 * Petición de Coil para una imagen remota del backend.
 *
 * Las imágenes son URLs firmadas de Cloudflare R2 que cambian en cada consulta
 * (X-Amz-Date / X-Amz-Signature) y vencen a los 15 min (X-Amz-Expires=900). Se usa
 * como clave de caché la URL sin query (el nombre del archivo es un UUID que no
 * cambia), así una imagen ya descargada se sirve de la caché aunque la firma venza.
 */
@Composable
private fun rememberRemoteImageRequest(url: String): ImageRequest {
    val context = LocalContext.current
    return remember(url) {
        val cacheKey = url.substringBefore('?')
        ImageRequest.Builder(context)
            .data(url)
            .memoryCacheKey(cacheKey)
            .diskCacheKey(cacheKey)
            .crossfade(true)
            .build()
    }
}

/**
 * Imagen remota con indicador de carga. Si no se puede cargar avisa con [onError]
 * para que quien la muestra oculte la sección (no se dibuja un estado de error).
 */
@Composable
private fun RemoteImage(
    url: String,
    contentDescription: String?,
    contentScale: ContentScale,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = rememberRemoteImageRequest(url),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        onError = { onError() }
    )
}

/** Visor a pantalla completa de una imagen remota (sin recortar). */
@Composable
private fun RemoteImageViewer(url: String, onError: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            RemoteImage(
                url = url,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                onError = onError,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(Dimens.PaddingSmall)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cerrar),
                    tint = Color.White
                )
            }
        }
    }
}

/** Archivo temporal en caché (expuesto por el FileProvider) para la foto de la cámara. */
private fun createPhotoUri(context: Context, prefix: String): Uri {
    val dir = File(context.cacheDir, "form_photos").apply { mkdirs() }
    val file = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
