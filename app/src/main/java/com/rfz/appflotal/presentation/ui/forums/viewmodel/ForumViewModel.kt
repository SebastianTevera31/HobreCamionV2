package com.rfz.appflotal.presentation.ui.forums.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.domain.forum.ForumUseCase
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
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
        val id = roomId.toIntOrNull() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            // Obtenemos los detalles del foro para asegurar que tenemos la info actualizada (título, imagen, etc)
            val forumResponse = forumUseCase.getForumsById(id)
            var currentRoom = _uiState.value.forums.find { it.id == id }

            asyncResponseHelper(forumResponse) { data ->
                data?.firstOrNull()?.let {
                    currentRoom = Topic(
                        id = it.idForum,
                        title = it.fldTitle,
                        description = it.fldDescription,
                        imageUrl = it.fldImage
                    )
                }
            }

            // Cargamos los temas (posts) del foro
            val topicsResponse = forumUseCase.getTopics(pageNumber = 1, idForum = id)
            asyncResponseHelper(
                topicsResponse,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar temas")) }
                }
            ) { data ->
                val posts = data?.results?.map {
                    Post(
                        id = it.idTopic,
                        title = it.fldTitle,
                        description = it.fldDescription,
                        imageUrl = it.fldImage,
                        author = it.fldUserName,
                        numComments = it.fldMessages,
                        time = it.fldRegistrationDate // Podrías formatear esta fecha si es necesario
                    )
                } ?: emptyList()

                _uiState.update {
                    it.copy(
                        screenState = LoadState.Success(Unit),
                        selectedRoom = currentRoom,
                        posts = posts
                    )
                }
            }
        }
    }

    fun loadTopicDetail(topicId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            // Buscamos el post en la lista actual o lo recuperamos de algún sitio si es necesario
            // Por ahora asumimos que viene de la lista de posts previa.
            val post = _uiState.value.posts.find { it.id == topicId }

            val response = forumUseCase.getTopicMessages(topicId)
            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar comentarios")) }
                }
            ) { data ->
                val comments = data?.map {
                    Comment(
                        id = it.idTopicMessages,
                        title = it.fldUserName,
                        description = it.fldMessage,
                        imageUrl = it.fldImage,
                        likes = it.fldLike,
                        isSaved = false, // Ajustar si hay endpoint de likes/saved
                        firstInitial = it.fldUserName.take(1).uppercase(),
                        secondInitial = ""
                    )
                } ?: emptyList()

                _uiState.update {
                    it.copy(
                        screenState = LoadState.Success(Unit),
                        selectedPost = post,
                        comments = comments
                    )
                }
            }
        }
    }

    fun onMenuClick() {

    }

    fun onSearchChanged(query: String) {

    }

    fun sendComment(commentText: String) {
        val topicId = _uiState.value.selectedPost?.id ?: return
        val capturedUri = (_uiState.value.photoEvidence as? CameraUiState.Captured)?.uri

        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            val response = forumUseCase.crudTopicMessage(
                idTopic = topicId,
                message = commentText,
                registrationDate = getCurrentDate(),
                image = "" // TODO: Implementar subida de imagen si es necesario
            )

            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al enviar comentario")) }
                }
            ) {
                // Recargar comentarios tras el éxito
                loadTopicDetail(topicId)
                clearPhoto()
            }
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