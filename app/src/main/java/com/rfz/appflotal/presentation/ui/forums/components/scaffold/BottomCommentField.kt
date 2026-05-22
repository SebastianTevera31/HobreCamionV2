package com.rfz.appflotal.presentation.ui.forums.components.scaffold

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun BottomCommentField(
    comment: String,
    onCommentChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    selectedImage: Uri? = null,
    onAddImage: () -> Unit = {},
    onRemoveImage: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (selectedImage != null) {
            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.PaddingSmall, vertical = 4.dp)
            ) {
                ImageThumbnail(
                    uri = selectedImage,
                    onRemove = onRemoveImage
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingSmall)
        ) {
//            IconButton(
//                onClick = onAddImage,
//                enabled = selectedImage == null, // Opcional: deshabilitar si ya hay una imagen
//                modifier = Modifier
//                    .shadow(
//                        elevation = 4.dp,
//                        shape = RoundedCornerShape(Dimens.PaddingSmall)
//                    )
//                    .size(48.dp),
//                shape = RoundedCornerShape(Dimens.PaddingSmall),
//                colors = IconButtonDefaults.iconButtonColors(
//                    containerColor = if (selectedImage == null)
//                        MaterialTheme.colorScheme.primary
//                    else
//                        MaterialTheme.colorScheme.outline
//                )
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Image,
//                    contentDescription = "Add image",
//                    tint = Color.White
//                )
//            }

            OutlinedTextField(
                value = comment,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                onValueChange = onCommentChange,
                shape = RoundedCornerShape(Dimens.PaddingSmall),
                placeholder = {
                    Text(
                        text = "Escribe un comentario...",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.3f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(Dimens.PaddingSmall)
                    )
                    .size(48.dp),
                shape = RoundedCornerShape(Dimens.PaddingSmall),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ImageThumbnail(
    uri: Uri,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-8).dp)
                .size(24.dp)
                .background(Color.Red, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomCommentFieldPreview() {
    HombreCamionTheme {
        BottomCommentField(
            comment = "Este es un comentario de prueba",
            onCommentChange = {},
            onSend = {},
            selectedImage = Uri.parse("https://example.com/image1.jpg")
        )
    }
}
