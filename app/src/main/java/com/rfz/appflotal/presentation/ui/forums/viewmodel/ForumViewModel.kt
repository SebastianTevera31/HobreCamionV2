package com.rfz.appflotal.presentation.ui.forums.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.forum.ForumResult
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.forum.ForumUseCase
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val forumUseCase: ForumUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(ForumUiState())
    val uiState = _uiState.asStateFlow()
    private var currentPhotoUri: Uri? = null

    fun getForums() {
        if (_uiState.value.forums.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }
            val response = forumUseCase.getForums(1)
            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar foros")) }
                }
            ) { data ->
                val forums = data?.results?.map {
                    Topic(
                        id = it.idForum,
                        title = it.fldTitle,
                        description = it.fldDescription,
                        imageUrl = it.fldImage
                    )
                } ?: emptyList()

                _uiState.update {
                    it.copy(
                        screenState = LoadState.Success(Unit),
                        forums = forums
                    )
                }
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

    fun sendComment(commentText: String) {
        val capturedUri = (_uiState.value.photoEvidence as? CameraUiState.Captured)?.uri
        viewModelScope.launch {
            // Aquí iría la lógica para enviar el comentario al repositorio
            // incluyendo el capturedUri si no es nulo
            
            // Simulación de éxito:
            delay(500)
            
            // Limpiar el estado de la foto después de enviar
            clearPhoto()
        }
    }

    fun startCamera(context: Context, onUriReady: (Uri) -> Unit) {
        currentPhotoUri = ImageUriFactory.create(context)
        changePhotoStatus(CameraUiState.TakingPhoto)
        onUriReady(currentPhotoUri!!)
    }

    fun onPhotoCaptured() {
        currentPhotoUri?.let { uri ->
            changePhotoStatus(CameraUiState.Captured(uri))
        }
    }

    fun onPhotoError(message: String) {
        changePhotoStatus(CameraUiState.Error(message))
    }

    fun clearPhoto() {
        currentPhotoUri = null
        changePhotoStatus(CameraUiState.Idle)
    }

    private fun changePhotoStatus(status: CameraUiState) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                photoEvidence = status
            )
        }
    }
}

object ImageUriFactory {
    fun create(context: Context): Uri {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "photo_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IllegalStateException("No se pudo crear URI")
    }
}