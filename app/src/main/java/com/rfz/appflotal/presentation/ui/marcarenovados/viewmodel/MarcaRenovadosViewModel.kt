package com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel

import androidx.compose.runtime.currentRecomposeScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import com.rfz.appflotal.domain.retreadbrand.RetreadBrand
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandCrudUseCase
import com.rfz.appflotal.domain.retreadbrand.RetreadBrandListUseCase
import com.rfz.appflotal.presentation.ui.common.viewmodel.ListManagementUiState
import com.rfz.appflotal.presentation.ui.common.viewmodel.ListManagementViewModel
import com.rfz.appflotal.presentation.ui.utils.responseHelperWithResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RetreadBrandFields(val value: String) {
    ID("idRetreatedBrand"),
    DESCRIPTION("description")
}

@HiltViewModel
class MarcaRenovadosViewModel @Inject constructor(
    private val retreadBrandCrudUseCase: RetreadBrandCrudUseCase,
    private val retreadBrandListUseCase: RetreadBrandListUseCase
) : ViewModel(), ListManagementViewModel<RetreadBrand> {

    private var _uiState = MutableStateFlow(ListManagementUiState<RetreadBrand>())
    override val uiState: StateFlow<ListManagementUiState<RetreadBrand>> =
        _uiState.asStateFlow()

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

    override fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filteredList = if (query.isBlank()) {
                currentState.originalItems
            } else {
                currentState.originalItems.filter { brand ->
                    brand.description.contains(query, ignoreCase = true) ||
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
                dialogData = emptyMap()
            )
        }
    }

    override fun onSaveItem() {
        viewModelScope.launch {
            val data = _uiState.value.dialogData
            val description = data[RetreadBrandFields.DESCRIPTION.value] as String
            val idRetreadBrand = data[RetreadBrandFields.ID.value] as String
            val response = retreadBrandCrudUseCase.invoke(
                RetreadBrandDto(
                    idRetreadBrand = idRetreadBrand.toInt(),
                    description = description
                )
            )

            responseHelperWithResult(response) {
                val items = _uiState.value.originalItems.toMutableList()
                items.add(
                    RetreadBrand(
                        id = idRetreadBrand.toInt(),
                        description = description
                    )
                )

                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        originalItems = items,
                        itemsToShow = items,
                    )
                }

                onDismissDialog()
            }
        }
    }

    override fun onIsEditing(isEditing: Boolean) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                isEditing = isEditing
            )
        }
    }

    override fun onDialogFieldChanged(field: String, value: Any?) {
        val newDialogData = _uiState.value.dialogData.toMutableMap()
        newDialogData[field] = value
        _uiState.update { currentUiState ->
            currentUiState.copy(
                dialogData = newDialogData
            )
        }
    }

    override fun setItemBrandById(id: Int) {
        val item = _uiState.value.originalItems.first { it.id == id }
        onDialogFieldChanged(RetreadBrandFields.ID.value, item.id.toString())
        onDialogFieldChanged(RetreadBrandFields.DESCRIPTION.value, item.description)
    }

    fun setTitle(title: String) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                title = title
            )
        }
    }
}