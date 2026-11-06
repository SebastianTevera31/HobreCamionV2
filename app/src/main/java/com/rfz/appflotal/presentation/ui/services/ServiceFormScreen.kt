package com.rfz.appflotal.presentation.ui.services

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.io.File
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CharacterCounter
import com.rfz.appflotal.presentation.ui.forums.components.ForumLimits
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.services.components.FieldLabel
import com.rfz.appflotal.presentation.ui.services.components.ServiceDropdownField
import com.rfz.appflotal.presentation.ui.services.components.ServiceTextField
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import com.rfz.appflotal.presentation.ui.services.model.toEditablePrice
import com.rfz.appflotal.presentation.ui.services.model.toMoney
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceTypes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class ServiceFormMode { CREATE, EDIT }

/** Costo unitario mientras se escribe: dígitos con hasta 2 decimales ("", "12", "12.", "12.5"). */
private val PRICE_INPUT = Regex("""\d*(\.\d{0,2})?""")

/** Estado editable del formulario de un servicio (mapea a ServiceDetailDto). */
data class ServiceFormData(
    val typeId: Int?,          // id_serviceType
    val description: String,   // fld_description (lo captura el usuario)
    val provider: String,      // fld_provider (lo captura el usuario)
    val price: String,         // fld_price
    val quantity: String,      // fld_cant
    val date: String,           // fld_date (ISO 8601)
    val notas: String,           // fld_notes
    val image: Uri? = null,      // imagen nueva elegida; null = conservar la actual
)

/**
 * Formulario compartido para "Nuevo servicio" (CREATE) y edición (EDIT).
 *
 * Nota: el endpoint de lectura no devuelve proveedor ni fecha, por lo que al
 * editar esos dos campos inician vacíos.
 */
@Composable
fun ServiceFormScreen(
    mode: ServiceFormMode,
    serviceTypes: List<CatalogItemUi>,
    onBack: () -> Unit,
    onSubmit: (ServiceFormData) -> Unit,
    modifier: Modifier = Modifier,
    initial: ServiceUi? = null
) {
    var typeName by remember(initial?.id) { mutableStateOf(initial?.type ?: "") }
    var typeId by remember(initial?.id, serviceTypes) {
        mutableStateOf(
            serviceTypes.firstOrNull {
                it.name.trim().equals(initial?.type?.trim(), ignoreCase = true)
            }?.id ?: serviceTypes.firstOrNull()?.id
        )
    }
    var description by remember(initial?.id) { mutableStateOf(initial?.description ?: "") }
    var provider by remember(initial?.id) { mutableStateOf(initial?.provider ?: "") }
    var price by remember(initial?.id) {
        mutableStateOf(
            initial?.price?.takeIf { it > 0 }?.toEditablePrice() ?: ""
        )
    }
    var quantity by remember(initial?.id) {
        mutableStateOf(initial?.quantity?.takeIf { it > 0 }?.toString() ?: "")
    }
    var dateMillis by remember(initial?.id) {
        mutableStateOf<Long?>(
            initial?.let { s ->
                parseDateToMillis(s.rawDate.ifBlank { s.date })
            }
        )
    }
    var notas by remember(initial?.id) { mutableStateOf(initial?.notes ?: "") }
    var newImage by rememberSaveable(initial?.id) { mutableStateOf<Uri?>(null) }

    val total = (price.toDoubleOrNull() ?: 0.0) * (quantity.toIntOrNull() ?: 0)
    val isValid = typeId != null && description.isNotBlank() &&
            (price.toDoubleOrNull() ?: 0.0) > 0 && (quantity.toIntOrNull() ?: 0) > 0 &&
            dateMillis != null

    val title = stringResource(
        if (mode == ServiceFormMode.CREATE) R.string.srv_form_nuevo_title
        else R.string.srv_form_detalle_title
    )
    val submitLabel = stringResource(
        if (mode == ServiceFormMode.CREATE) R.string.srv_agregar_servicio_btn
        else R.string.srv_actualizar_btn
    )

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { SimpleTopBar(title = title, onBack = onBack, showBackButton = true) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            ServiceDropdownField(
                label = stringResource(R.string.srv_tipo_servicio_label),
                selected = typeName,
                placeholder = stringResource(R.string.srv_servicio_seleccionar),
                options = serviceTypes.map { it.name },
                onSelect = {
                    typeName = serviceTypes[it].name
                    typeId = serviceTypes[it].id
                }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_servicio_label),
                value = description,
                onValueChange = { description = it }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_proveedor_label),
                value = provider,
                onValueChange = { provider = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                ServiceTextField(
                    label = stringResource(R.string.srv_costo_unitario_label),
                    value = price,
                    // Decimal con hasta 2 dígitos; algunos teclados escriben coma.
                    onValueChange = { input ->
                        val normalized = input.replace(',', '.')
                        if (PRICE_INPUT.matches(normalized)) price = normalized
                    },
                    prefix = "$",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )

                ServiceTextField(
                    label = stringResource(R.string.srv_cantidad_label),
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            ServiceDateField(
                label = stringResource(R.string.fecha),
                dateMillis = dateMillis,
                onDateSelected = { dateMillis = it }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_total_label),
                value = total.toMoney(),
                onValueChange = {},
                enabled = false
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FieldLabel(
                    stringResource(R.string.notas),
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it.take(ForumLimits.TEXT_MAX_LENGTH) },
                    shape = RoundedCornerShape(Dimens.PaddingSmall),
                    placeholder = {
                        Text(
                            text = stringResource(
                                R.string.agregar_notas
                            )
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor =
                            MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor =
                            MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                )
                CharacterCounter(current = notas.length, max = ForumLimits.TEXT_MAX_LENGTH)
            }

            ServiceImageField(
                editable = mode == ServiceFormMode.CREATE,
                newImage = newImage,
                currentImageUrl = initial?.imageUrl,
                onImageSelected = { newImage = it },
                onRemoveNewImage = { newImage = null }
            )

            Button(
                onClick = {
                    onSubmit(
                        ServiceFormData(
                            typeId = typeId,
                            description = description,
                            provider = provider,
                            price = price,
                            quantity = quantity,
                            date = dateMillis.toIsoDate(),
                            notas = notas,
                            image = newImage,
                        )
                    )
                },
                enabled = isValid,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(submitLabel, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/**
 * Imagen del servicio.
 *
 * - Alta ([editable] = true): se toma con la cámara o se elige de la galería, con
 *   vista previa y opción de quitarla.
 * - Detalle ([editable] = false): solo muestra la imagen registrada
 *   ([currentImageUrl], de fld_image), sin botones. Si no tiene, o si el enlace no
 *   se puede cargar (recurso inalcanzable, firma vencida), la sección no se muestra.
 */
@Composable
private fun ServiceImageField(
    editable: Boolean,
    newImage: Uri?,
    currentImageUrl: String?,
    onImageSelected: (Uri) -> Unit,
    onRemoveNewImage: () -> Unit,
    modifier: Modifier = Modifier
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
        ServiceImageViewer(url = remoteUrl, onError = onRemoteError) { showViewer = false }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        FieldLabel(stringResource(R.string.srv_imagen_label))

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
                        contentDescription = stringResource(R.string.srv_imagen_label),
                        contentScale = ContentScale.Crop,
                        modifier = previewModifier
                    )
                } else if (remoteUrl != null) {
                    RemoteServiceImage(
                        url = remoteUrl,
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
                    val uri = createServicePhotoUri(context)
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
 * Petición de Coil para una imagen de fld_image.
 *
 * fld_image es una URL firmada de Cloudflare R2 que cambia en cada GetServices
 * (X-Amz-Date / X-Amz-Signature) y vence a los 15 min (X-Amz-Expires=900). Se usa
 * como clave de caché la URL sin query (el nombre del archivo es un UUID que no
 * cambia), así una imagen ya descargada se sirve de la caché aunque la firma venza.
 */
@Composable
private fun rememberServiceImageRequest(url: String): ImageRequest {
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
private fun RemoteServiceImage(
    url: String,
    contentScale: ContentScale,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = rememberServiceImageRequest(url),
        contentDescription = stringResource(R.string.srv_imagen_label),
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

/** Visor a pantalla completa de la imagen del servicio (sin recortar). */
@Composable
private fun ServiceImageViewer(url: String, onError: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            RemoteServiceImage(
                url = url,
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
private fun createServicePhotoUri(context: Context): Uri {
    val dir = File(context.cacheDir, "service_photos").apply { mkdirs() }
    val file = File(dir, "service_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

/** Campo de solo lectura que abre un selector de fecha. */
@Composable
private fun ServiceDateField(
    label: String,
    dateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ServiceTextField(
            label = label,
            value = dateMillis?.let { formatDisplayDate(it) } ?: "",
            onValueChange = {},
            placeholder = stringResource(R.string.seleccionar_fecha),
            enabled = false
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        LocalizedApp {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let(onDateSelected)
                        showDialog = false
                    }) { Text(stringResource(R.string.confirmar)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

private fun formatDisplayDate(millis: Long): String {
    val sdf = SimpleDateFormat(Commons.displayDatePattern(), Commons.displayLocale())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

private fun parseDateToMillis(dateStr: String): Long? {
    if (dateStr.isBlank()) return null
    val patterns = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd",
        Commons.displayDatePattern(),
        "dd/MM/yyyy",
        "d 'de' MMMM, yyyy",
        "MMMM d, yyyy"
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            if (pattern.contains("Z") || pattern.contains("T")) {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            val parsed = sdf.parse(dateStr)
            if (parsed != null) return parsed.time
        } catch (_: Exception) {
            // Continuar con el siguiente patrón
        }
    }
    return null
}

/** Fecha en el formato ISO 8601 UTC que espera el backend (fld_date). */
private fun Long?.toIsoDate(): String {
    if (this == null) return ""
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(this))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServiceFormCreatePreview() {
    HombreCamionTheme {
        ServiceFormScreen(
            mode = ServiceFormMode.CREATE,
            serviceTypes = sampleServiceTypes,
            onBack = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServiceFormEditPreview() {
    HombreCamionTheme {
        ServiceFormScreen(
            mode = ServiceFormMode.EDIT,
            serviceTypes = sampleServiceTypes,
            onBack = {},
            onSubmit = {},
            initial = com.rfz.appflotal.presentation.ui.services.model.sampleServices.first()
        )
    }
}
