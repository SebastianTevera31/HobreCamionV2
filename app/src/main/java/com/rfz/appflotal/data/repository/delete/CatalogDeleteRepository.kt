package com.rfz.appflotal.data.repository.delete


import com.rfz.appflotal.data.model.delete.CatalogDeleteDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.client.delete.CatalogDeleteClient
import jakarta.inject.Inject

class CatalogDeleteRepository @Inject constructor(private val client: CatalogDeleteClient) {

    suspend fun deleteCatalog(dto: CatalogDeleteDto, token: String): Result<List<MessageResponse>> {
        return try {
            val response = client.deleteCatalog(token, dto)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
