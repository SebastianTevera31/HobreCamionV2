package com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel

import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import com.rfz.appflotal.domain.retreadbrand.RetreadBrand
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandCrudUseCase
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandListUseCase
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ListManagementUiState
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ListManagementViewModel
import com.rfz.appflotal.presentation.ui.utils.responseHelperWithResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UploadingItemMessage(@StringRes val message: Int) {
    SUCCESS(R.string.marca_de_renovado_guardada),
    GENERAL_ERROR(R.string.error_guardar_marca_renovado),
    NO_VALID_ID_ERROR(R.string.error_id_no_valida)
}

enum class RetreadBrandFields { ID, DESCRIPTION }

data class ShowToast(@StringRes val message: Int)

data class RetreadBrandDialogState(
    val id: String = "0",
    val description: String = ""
)

@HiltViewModel
class MarcaRenovadosViewModel @Inject constructor(
    private val retreadBrandCrudUseCase: RetreadBrandCrudUseCase,
    private val retreadBrandListUseCase: RetreadBrandListUseCase
) : ViewModel(), ListManagementViewModel<RetreadBrand> {

    private var _uiState = MutableStateFlow(ListManagementUiState<RetreadBrand>())
    override val uiState: StateFlow<ListManagementUiState<RetreadBrand>> =
        _uiState.asStateFlow()

    private var _dialogState = MutableStateFlow(RetreadBrandDialogState())
    val dialogState: StateFlow<RetreadBrandDialogState> = _dialogState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ShowToast>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadItems()
    }

    override fun loadItems() {
        viewModelScope.launch {
            val result = retreadBrandListUseCase.invoke()
            responseHelperWithResult(result) { response ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        originalItems = response.map { it.toDomain() },
                        itemsToShow = response.map { it.toDomain() },
                        isLoading = false
                    )
                }
            }
        }
    }

    override fun setTitle(title: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                title = title
            )
        }
    }

    override fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filteredList = if (query.isBlank()) {
                currentState.originalItems
            } else {
                currentState.originalItems.filter { brand ->
                    brand.description.contains(query.trim(), ignoreCase = true) ||
                            brand.id.toString().contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                itemsToShow = filteredList
            )
        }
    }

    override fun onClearQuery() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                searchQuery = "",
                itemsToShow = currentUiState.originalItems
            )
        }
    }

    override fun onShowDialog() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                showDialog = true
            )
        }
    }

    override fun onDismissDialog() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                showDialog = false,
            )
        }
        _dialogState.value = RetreadBrandDialogState()
    }

    override fun onAddItem() {
        onSaveItem(
            onRequest = { id, description ->
                retreadBrandCrudUseCase.invoke(
                    RetreadBrandDto(
                        idRetreadBrand = id.toInt(),
                        description = description.trim()
                    )
                )
            }
        )
    }

    override fun onUpdateItem() {
        onSaveItem(
            onRequest = { id, description ->
                retreadBrandCrudUseCase.invoke(
                    RetreadBrandDto(
                        idRetreadBrand = id.toInt(),
                        description = description.trim()
                    )
                )
            }
        )
    }

    override fun onEditing(isEditing: Boolean) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                isEditing = isEditing
            )
        }
    }

    private fun onSaveItem(
        onRequest: suspend (id: String, description: String) -> Result<List<MessageResponse>>
    ) {
        _uiState.update { it.copy(isSending = true) }
        viewModelScope.launch {
            val data = _dialogState.value
            val id = data.id.trim()
            val description = data.description.trim()

            if (!id.isDigitsOnly()) {
                _eventFlow.emit(ShowToast(UploadingItemMessage.NO_VALID_ID_ERROR.message))
                onDismissDialog()
                return@launch
            }

            val response = onRequest(id, description)

            _uiState.update { it.copy(isSending = false) }

            if (response.isSuccess) {
                _eventFlow.emit(ShowToast(UploadingItemMessage.SUCCESS.message))

                loadItems()
            } else {
                _eventFlow.emit(ShowToast(UploadingItemMessage.GENERAL_ERROR.message))
            }

            onDismissDialog()
        }
    }

    fun onDialogFieldChanged(field: RetreadBrandFields, value: String) {
        _dialogState.update { currentState ->
            val newDialogState = when (field) {
                RetreadBrandFields.ID -> currentState.copy(id = value)
                RetreadBrandFields.DESCRIPTION -> currentState.copy(description = value)
            }
            newDialogState
        }
    }

    fun setItemBrandById(item: RetreadBrand) {
        onEditing(true)
        _dialogState.update { currentState ->
            currentState.copy(
                item.id.toString().trim(),
                item.description.trim()
            )
        }
    }
}