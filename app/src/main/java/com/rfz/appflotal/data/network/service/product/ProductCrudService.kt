package com.rfz.appflotal.data.network.service.product

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.network.client.disassembly.DisassemblyCauseClient
import com.rfz.appflotal.data.network.client.product.ProductCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class ProductCrudService @Inject constructor(private val productCrudClient: ProductCrudClient) {
    suspend fun doCrudProduct(
        requestBody: ProductCrudDto,
        tok: String
    ): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            productCrudClient.doCrudProduct(requestBody, tok)
        }
    }
}