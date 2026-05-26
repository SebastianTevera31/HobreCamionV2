package com.rfz.appflotal.presentation.ui.forums.screen.savedcomments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.forum.LikedRecord
import com.rfz.appflotal.data.model.forum.toEntity
import com.rfz.appflotal.domain.forum.DoForumLikeUseCase
import com.rfz.appflotal.domain.forum.GetLikedPostsUseCase
import com.rfz.appflotal.presentation.ui.forums.viewmodel.RecordType
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedCommentsUiState(
    val comments: List<LikedRecord> = emptyList(),
    val savedCommentState: LoadState<Unit> = LoadState.Idle
)

@HiltViewModel
class SavedCommentsViewModel @Inject constructor(
    private val getLikedPostsUseCase: GetLikedPostsUseCase,
    private val doForumLikeUseCase: DoForumLikeUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(SavedCommentsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadComments() {
        viewModelScope.launch {
            _uiState.update { it.copy(savedCommentState = LoadState.Loading) }
            val response = getLikedPostsUseCase()
            asyncResponseHelper(response, {
                _uiState.update { it.copy(savedCommentState = LoadState.Error("Error al cargar comentarios")) }
            }) { result ->
                val comments = result.mapNotNull { record -> record.toEntity() }
                _uiState.update {
                    it.copy(
                        comments = comments,
                        savedCommentState = LoadState.Success(Unit)
                    )
                }
            }
        }
    }

    fun deleteComment(idMessage: Int, idRecord: Int, type: RecordType) {
        viewModelScope.launch {
            val response = doForumLikeUseCase(
                likedDate = getCurrentDate(),
                tipoElemento = type.isComment,
                idMessage = idMessage
            )

            asyncResponseHelper(
                response,
                onError = {
                    // Manejar error si es necesario
                }
            ) {
                _uiState.update { currentUiState ->
                    val updatedComments = currentUiState.comments.filter { it.likedId != idRecord }
                    currentUiState.copy(comments = updatedComments)
                }
            }
        }
    }

    fun clearSavedCommentState() {
        _uiState.update { it.copy(savedCommentState = LoadState.Idle, comments = emptyList()) }
    }
}