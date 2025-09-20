package com.rfz.appflotal.data.network.client.delete


import com.rfz.appflotal.data.model.delete.CatalogDeleteDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.Header

interface CatalogDeleteClient {
    @HTTP(method = "DELETE", path = "api/Catalog/Delete", hasBody = true)
    suspend fun deleteCatalog(
        @Header("Authorization") token: String,
        @Body dto: CatalogDeleteDto
    ): List<MessageResponse>
}
