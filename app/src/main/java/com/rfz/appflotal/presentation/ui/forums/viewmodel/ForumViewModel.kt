package com.rfz.appflotal.presentation.ui.forums.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.getRelativeTime
import com.rfz.appflotal.data.model.forum.ForumComment
import com.rfz.appflotal.data.model.forum.ForumTopic
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.forum.CreateForumReportUseCase
import com.rfz.appflotal.domain.forum.CrudForumCommentUseCase
import com.rfz.appflotal.domain.forum.CrudForumTopicUseCase
import com.rfz.appflotal.domain.forum.DoForumLikeUseCase
import com.rfz.appflotal.domain.forum.GetForumRoomWithTopicsUseCase
import com.rfz.appflotal.domain.forum.GetForumRoomsUseCase
import com.rfz.appflotal.domain.forum.GetForumTopicByIdUseCase
import com.rfz.appflotal.domain.forum.GetForumTopicMessagesUseCase
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val getForumRoomsUseCase: GetForumRoomsUseCase,
    private val getForumRoomWithTopicsUseCase: GetForumRoomWithTopicsUseCase,
    private val getForumTopicByIdUseCase: GetForumTopicByIdUseCase,
    private val getForumTopicMessagesUseCase: GetForumTopicMessagesUseCase,
    private val doForumLikeUseCase: DoForumLikeUseCase,
    private val crudForumTopicUseCase: CrudForumTopicUseCase,
    private val createForumReportUseCase: CreateForumReportUseCase,
    private val crudForumCommentUseCase: CrudForumCommentUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(ForumUiState())
    val uiState = _uiState.asStateFlow()
    private var currentPhotoUri: Uri? = null
    private var publicationJob: Job? = null

    fun getInitialData(forceRefresh: Boolean = false) {
        if (_uiState.value.rooms.isNotEmpty() && !forceRefresh) return
        viewModelScope.launch {
            _uiState.update { it.copy(roomState = LoadState.Loading) }

            val roomsResponse = getForumRoomsUseCase(1)

            asyncResponseHelper(roomsResponse, onError = {
                _uiState.update { it.copy(roomState = LoadState.Error("Error al cargar salas")) }
            }) { rooms ->
                _uiState.update {
                    it.copy(
                        roomState = LoadState.Success(Unit),
                        rooms = rooms,
                        filteredRooms = rooms,
                    )
                }
            }
        }
    }

    fun loadTopicsByRoom(roomId: String) {
        val id = roomId.toIntOrNull() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }

            val response = getForumRoomWithTopicsUseCase(id)
            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar temas")) }
                }
            ) { result ->
                val (currentRoom, topics) = result
                _uiState.update {
                    it.copy(
                        screenState = LoadState.Success(Unit),
                        selectedRoom = currentRoom ?: it.rooms.find { f -> f.id == id },
                        topics = topics,
                        filteredTopics = topics
                    )
                }
            }
        }
    }

    fun loadTopicMessages(topicId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = LoadState.Loading) }
            val topicResponse = getForumTopicByIdUseCase(topicId)
            val response = getForumTopicMessagesUseCase(topicId)

            asyncResponseHelper(
                topicResponse,
                onError = {
                    _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar comentarios")) }
                }
            ) { topic ->
                asyncResponseHelper(
                    response,
                    onError = {
                        _uiState.update { it.copy(screenState = LoadState.Error("Error al cargar comentarios")) }
                    }
                ) { comments ->
                    _uiState.update {
                        it.copy(
                            screenState = LoadState.Success(Unit),
                            selectedTopic = topic,
                            comments = comments
                        )
                    }
                }
            }
        }
    }

    fun onSearchChanged(query: String, screenType: ForumScreenType) {
        _uiState.update { it.copy(searchQuery = query) }

        if (screenType == ForumScreenType.ROOM) {
            val list = if (query.isEmpty()) {
                _uiState.value.rooms
            } else {
                _uiState.value.rooms.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    filteredRooms = list
                )
            }
        } else if (screenType == ForumScreenType.TOPIC) {
            val list = if (query.isEmpty()) {
                _uiState.value.topics
            } else {
                _uiState.value.topics.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    filteredTopics = list
                )
            }
        }
    }

    fun clearFilterSearch() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                filteredTopics = currentUiState.topics,
                filteredRooms = currentUiState.rooms,
                searchQuery = ""
            )
        }
    }

    fun doLike(id: Int, isComment: Boolean, fromPostsView: Boolean = false) {
        viewModelScope.launch {
            val response = doForumLikeUseCase(
                likedDate = getCurrentDate(),
                tipoElemento = isComment,
                idMessage = id
            )

            asyncResponseHelper(
                response,
                onError = {
                    // Manejar error si es necesario
                }
            ) {
                // Actualizar UI localmente o recargar datos
                if (!isComment) {
                    if (fromPostsView) {
                        _uiState.update { currentUiState ->
                            val updatedTopics = currentUiState.filteredTopics.map { topic ->
                                if (topic.id == id) {
                                    val newIsSaved = !topic.isLiked
                                    topic.copy(
                                        isLiked = newIsSaved,
                                        likes = if (newIsSaved) topic.likes + 1 else topic.likes - 1
                                    )
                                } else {
                                    topic
                                }
                            }
                            currentUiState.copy(filteredTopics = updatedTopics)
                        }
                    } else {
                        val currentTopicId = _uiState.value.selectedTopic?.id
                        if (currentTopicId != null) {
                            _uiState.update { currentUiState ->
                                val newIsSaved = !currentUiState.selectedTopic!!.isLiked
                                val topic = currentUiState.selectedTopic
                                currentUiState.copy(
                                    selectedTopic = topic.copy(
                                        isLiked = newIsSaved,
                                        likes = if (newIsSaved) topic.likes + 1 else topic.likes - 1
                                    )
                                )
                            }
                        }
                    }
                } else {
                    _uiState.update { currentUiState ->
                        val updatedComments = currentUiState.comments.map { comment ->
                            if (comment.id == id) {
                                val newIsSaved = !comment.isLiked
                                comment.copy(
                                    isLiked = newIsSaved,
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

    fun sendTopic(
        title: String,
        description: String,
        tags: String,
        color: String
    ) {
        publicationJob?.cancel()
        publicationJob = viewModelScope.launch {
            val currentRoomId = _uiState.value.selectedRoom?.id ?: 0
            if (currentRoomId == 0) {
                _uiState.update { it.copy(newTopicState = LoadState.Error("No se ha seleccionado una sala válida.")) }
                return@launch
            }

            _uiState.update { it.copy(newTopicState = LoadState.Loading) }

            val response = crudForumTopicUseCase(
                title = title,
                description = description,
                color = color,
                image = "",
                idForum = currentRoomId,
                tags = tags,
                registrationDate = getCurrentDate()
            )

            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(newTopicState = LoadState.Error("Error al publicar el tema")) }
                }
            ) {
                // Actualizamos localmente para evitar el GET.
                val userList = getTasksUseCase().first()
                if (userList.isNotEmpty()) {
                    val user = userList.first()

                    _uiState.update { state ->
                        val newTopic = ForumTopic(
                            id = -(state.topics.size + 1), // ID temporal único
                            title = title,
                            description = description,
                            imageUrl = "",
                            author = user.fld_username,
                            numComments = 0,
                            time = getRelativeTime(getCurrentDate()),
                            idUser = user.idUser,
                            color = Color(color.toColorInt()),
                            isLiked = false,
                            likes = 0
                        )
                        val newList = state.topics + newTopic
                        state.copy(
                            newTopicState = LoadState.Success(Unit),
                            topics = newList,
                            filteredTopics = newList
                        )
                    }
                }
            }
        }
    }

    fun resetNewTopicState() {
        _uiState.update { it.copy(newTopicState = LoadState.Idle) }
    }

    fun resetCommentState() {
        _uiState.update { it.copy(sendCommentState = LoadState.Idle) }
    }

    fun resetReportState() {
        _uiState.update { it.copy(reportState = LoadState.Idle) }
    }

    fun cancelPublication() {
        publicationJob?.cancel()
        _uiState.update {
            it.copy(
                newTopicState = LoadState.Idle,
                sendCommentState = LoadState.Idle
            )
        }
    }

    fun sendReport(id: Int, isComment: Boolean, reportTypeId: Int, details: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(reportState = LoadState.Loading) }

            val response = createForumReportUseCase(
                tipoElemento = isComment,
                idElemento = id,
                reportTypeId = reportTypeId,
                reportDate = getCurrentDate()
            )

            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(reportState = LoadState.Error("Error al enviar reporte")) }
                }
            ) {
                _uiState.update { it.copy(reportState = LoadState.Success(Unit)) }
            }
        }
    }

    fun sendComment(commentText: String) {
        val topicId = _uiState.value.selectedTopic?.id ?: return

        publicationJob?.cancel()
        publicationJob = viewModelScope.launch {
            _uiState.update { it.copy(sendCommentState = LoadState.Loading) }

            val response = crudForumCommentUseCase(
                idTopic = topicId,
                message = commentText,
                registrationDate = getCurrentDate(),
                image = ""
            )

            asyncResponseHelper(
                response,
                onError = {
                    _uiState.update { it.copy(sendCommentState = LoadState.Error("Error al enviar comentario")) }
                }
            ) {
                val userList = getTasksUseCase().first()
                if (userList.isNotEmpty()) {
                    val user = userList.first()
                    val (first, second) = Commons.getInitials(user.fld_username)

                    _uiState.update { state ->
                        val newComment = ForumComment(
                            id = -(state.comments.size + 1), // ID temporal único
                            title = user.fld_username,
                            description = commentText,
                            imageUrl = "",
                            time = getRelativeTime(getCurrentDate()),
                            likes = 0,
                            firstInitial = first,
                            secondInitial = second,
                            isLiked = false,
                            idUser = user.idUser
                        )

                        val selectedTopicId = state.selectedTopic?.id

                        val updateTopics = { list: List<ForumTopic> ->
                            list.map { topic ->
                                if (selectedTopicId != null && topic.id == selectedTopicId) {
                                    topic.copy(numComments = topic.numComments + 1)
                                } else {
                                    topic
                                }
                            }
                        }

                        state.copy(
                            sendCommentState = LoadState.Success(Unit),
                            comments = state.comments + newComment,
                            topics = updateTopics(state.topics),
                            filteredTopics = updateTopics(state.filteredTopics),
                            selectedTopic = state.selectedTopic?.copy(
                                numComments = state.selectedTopic.numComments + 1
                            )
                        )
                    }
                    clearPhoto()
                }
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

    fun clearScreenState() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                screenState = LoadState.Idle
            )
        }
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
