package com.rfz.appflotal.presentation.ui.forums.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.model.forum.ForumRoom
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.ForumShimmerList
import com.rfz.appflotal.presentation.ui.forums.screen.post.ForumCard
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun RoomsScreen(
    rooms: List<ForumRoom>,
    modifier: Modifier = Modifier,
    loadState: LoadState<Any> = LoadState.Idle,
    onNavigate: (ForumRoom) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (loadState is LoadState.Loading) {
            ForumShimmerList()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(Dimens.PaddingSmall),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rooms) { room ->
                    ForumCard(
                        title = room.title,
                        content = room.description,
                        imageUrl = room.imageUrl,
                        modifier = Modifier.padding(Dimens.PaddingExtraSmall),
                        onNav = { onNavigate(room) },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RoomsScreenPreview() {
    val sampleRooms = listOf(
        ForumRoom(
            id = 1,
            title = "Cómo cambiar una llanta",
            description = "En este tutorial aprenderás los pasos básicos para cambiar una llanta de forma segura.",
            imageUrl = ""
        ),
        ForumRoom(
            id = 2,
            title = "Mantenimiento preventivo",
            description = "La importancia de revisar los frenos y el aceite regularmente para evitar averías mayores.",
            imageUrl = ""
        ),
        ForumRoom(
            id = 3,
            title = "Rutas recomendadas",
            description = "Descubre las mejores rutas para transportistas este verano, con paradas seguras y buenos servicios.",
            imageUrl = ""
        )
    )
    HombreCamionTheme {
        RoomsScreen(rooms = sampleRooms) {}
    }
}
