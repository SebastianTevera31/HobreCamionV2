package com.rfz.appflotal.presentation.ui.forums.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(ForumUiState())
    val uiState = _uiState.asStateFlow()

    fun getForums() {
        if (_uiState.value.forums.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }
            delay(1000) // Simular red
            _uiState.update {
                it.copy(
                    screenState = LoadState.Success(Unit),
                    forums = listOf(
                        Topic(1, "Mecánica General", "Todo sobre mantenimiento y averías", ""),
                        Topic(2, "Rutas y Logística", "Mejores paradas y estados de vía", ""),
                        Topic(3, "Seguridad Vial", "Consejos de conducción segura", "")
                    )
                )
            }
        }
    }

    fun loadPostsByRoom(roomId: String) {
        if (_uiState.value.selectedRoom?.id?.toString() == roomId && _uiState.value.posts.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }
            delay(1000) // Simular red
            val room = _uiState.value.forums.find { it.id.toString() == roomId }
            _uiState.update {
                it.copy(
                    screenState = LoadState.Success(Unit),
                    selectedRoom = room,
                    posts = listOf(
                        Post(
                            1,
                            "Ruido en motor DT12",
                            "Siento un golpeteo al arrancar...",
                            "",
                            "Juan Pérez",
                            5,
                            "2h"
                        ),
                        Post(
                            2,
                            "Mejor ruta para el Norte",
                            "¿Cómo está la vía por el paso?",
                            "",
                            "Carlos Ruiz",
                            3,
                            "5h"
                        )
                    )
                )
            }
        }
    }

    fun loadTopicDetail(topicId: Int) {
        if (_uiState.value.selectedPost?.id == topicId && _uiState.value.comments.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }
            delay(1000) // Simular red
            
            // Intentar encontrar el post en la lista actual, o crear uno temporal si no existe (ej. navegación directa)
            val post = _uiState.value.posts.find { it.id == topicId } ?: Post(
                id = topicId,
                title = "Tema #$topicId",
                description = "Cargando descripción...",
                imageUrl = "",
                author = "Usuario",
                numComments = 0,
                time = "Reciente"
            )

            _uiState.update {
                it.copy(
                    screenState = LoadState.Success(Unit),
                    selectedPost = post,
                    comments = listOf(
                        Comment(1, "Admin", "Revisa los filtros de aceite.", "", 12, true, "A", ""),
                        Comment(
                            2,
                            "Ana Martínez",
                            "A mí me pasó algo parecido y era la correa.",
                            "",
                            5,
                            false,
                            "A",
                            "M"
                        )
                    )
                )
            }
        }
    }

    fun onMenuClick() {

    }

    fun onSearchChanged(query: String) {

    }
}