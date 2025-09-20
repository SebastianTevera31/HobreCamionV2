package com.rfz.appflotal.domain.delete


import com.rfz.appflotal.data.model.delete.CatalogDeleteDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.repository.delete.CatalogDeleteRepository

import javax.inject.Inject

class CatalogDeleteUseCase @Inject constructor(
    private val repository: CatalogDeleteRepository
) {
    suspend operator fun invoke(dto: CatalogDeleteDto, token: String): Result<List<MessageResponse>> {
        return repository.deleteCatalog(dto, token)
    }
}
