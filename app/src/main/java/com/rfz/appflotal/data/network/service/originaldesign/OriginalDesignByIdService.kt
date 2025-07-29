package com.rfz.appflotal.data.network.service.originaldesign

import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignByIdResponse
import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.network.client.originaldesign.CrudOriginalDesignClient
import com.rfz.appflotal.data.network.client.originaldesign.OriginalDesignByIdClient
import com.rfz.appflotal.data.network.client.product.ProductByIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class OriginalDesignByIdService @Inject constructor(private val originalDesignByIdClient: OriginalDesignByIdClient) {
    suspend fun doOriginalDesignById(
        id: Int,
        tok: String
    ): Response<List<OriginalDesignByIdResponse>> {
        return withContext(Dispatchers.IO) {
            originalDesignByIdClient.doOriginalDesignById(id, tok)
        }
    }
}