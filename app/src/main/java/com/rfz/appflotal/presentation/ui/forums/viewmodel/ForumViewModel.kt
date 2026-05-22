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
            ) { forums ->
                _uiState.update {
                    it.copy(
                        screenState = LoadState.Success(Unit),
                        forums = forums,
                        filteredForums = forums
                    )
                }
            }
        }
    }

    fun loadPostsByRoom(roomId: String) {
        val id = roomId.toIntOrNull() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            val response = forumUseCase.getRoomWithPosts(id)
            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar temas")) }
                }
            ) { result ->
                val (currentRoom, posts) = result
                _uiState.update {
                    it.copy(
                        screenState = LoadState.Success(Unit),
                        selectedRoom = currentRoom ?: it.forums.find { f -> f.id == id },
                        posts = posts,
                        filteredPosts = posts
                    )
                }
            }
        }
    }

    fun loadTopicDetail(topicId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            val post = _uiState.value.posts.find { it.id == topicId }
            val response = forumUseCase.getTopicMessages(topicId)
            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar comentarios")) }
                }
            ) { comments ->
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

    fun onSearchChanged(query: String, screenType: ForumScreenType) {
        _uiState.update { it.copy(searchQuery = query) }

        if (screenType == ForumScreenType.TOPIC) {
            val list = if (query.isEmpty()) {
                _uiState.value.forums
            } else {
                _uiState.value.forums.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    filteredForums = list
                )
            }
        } else if (screenType == ForumScreenType.POST) {
            val list = if (query.isEmpty()) {
                _uiState.value.posts
            } else {
                _uiState.value.posts.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    filteredPosts = list
                )
            }
        }
    }

    fun clearFilterSearch() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                filteredPosts = currentUiState.posts,
                filteredForums = currentUiState.forums,
                searchQuery = ""
            )
        }
    }

    fun doLike(postId: Int, isPost: Boolean) {
        viewModelScope.launch {
            val response = forumUseCase.doLike(
                likedDate = getCurrentDate(),
                idTopic = if (isPost) postId else 0,
                idMessage = if (!isPost) postId else 0
            )

            asyncResponseHelper(
                response,
                onError = {
                    // Manejar error si es necesario
                }
            ) {
                // Actualizar UI localmente o recargar datos
                if (isPost) {
                    val currentTopicId = _uiState.value.selectedPost?.id
                    if (currentTopicId != null) {
                        _uiState.update { currentUiState ->
                            val post = _uiState.value.selectedPost!!.copy(isSaved = true)
                            currentUiState.copy(selectedPost = post)
                        }
                    }
                } else {
                    _uiState.update { currentUiState ->
                        val updatedComments = currentUiState.comments.map { comment ->
                            if (comment.id == postId) {
                                val newIsSaved = !comment.isSaved
                                comment.copy(
                                    isSaved = newIsSaved,
                                    likes = if (newIsSaved) comment.likes + 1 else comment.likes - 1
                                )
                            } else {
                                comment
                            }
                        }
                        currentUiState.copy(comments = updatedComments)
                    }
                }
            }
        }
    }

    fun sendPost(
        title: String,
        description: String,
        tags: String,
        color: String
    ) {
        viewModelScope.launch {
            val currentForumId = _uiState.value.selectedRoom?.id ?: 0
            if (currentForumId == 0) {
                _uiState.update { it.copy(newTopicState = LoadState.Error("No se ha seleccionado un foro válido.")) }
                return@launch
            }

            _uiState.update { it.copy(newTopicState = LoadState.Loading) }

            val response = forumUseCase.crudPost(
                title = title,
                description = description,
                color = color,
                image = "",
                idForum = currentForumId,
                tags = tags,
                registrationDate = getCurrentDate()
            )

            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(newTopicState = LoadState.Error("Error al publicar el tema")) }
                }
            ) {
                _uiState.update { it.copy(newTopicState = LoadState.Success(Unit)) }
                // Recargamos los posts de la sala actual para que al volver se vea el nuevo
                loadPostsByRoom(currentForumId.toString())
            }
        }
    }

    fun resetNewTopicState() {
        _uiState.update { it.copy(newTopicState = LoadState.Idle) }
    }

    fun sendComment(commentText: String) {
        val topicId = _uiState.value.selectedPost?.id ?: return
        val capturedUri = (_uiState.value.photoEvidence as? CameraUiState.Captured)?.uri

        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            val response = forumUseCase.crudComment(
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