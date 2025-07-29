package com.rfz.appflotal.data.network.service.originaldesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.network.client.originaldesign.CrudOriginalDesignClient
import com.rfz.appflotal.data.network.client.product.ProductCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class CrudOriginalDesignService @Inject constructor(private val crudOriginalDesignClient: CrudOriginalDesignClient) {
    suspend fun doCrudOriginalDesign(
        requestBody: CrudOriginalDesignDto,
        tok: String
    ): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            crudOriginalDesignClient.doCrudOriginalDesign(requestBody, tok)
        }
    }
}