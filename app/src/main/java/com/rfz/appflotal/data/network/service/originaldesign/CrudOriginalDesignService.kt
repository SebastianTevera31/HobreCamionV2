package com.rfz.appflotal.data.network.service.originaldesign

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.network.client.originaldesign.CrudOriginalDesignClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class CrudOriginalDesignService @Inject constructor(private val crudOriginalDesignClient: CrudOriginalDesignClient) {
    suspend fun doCrudOriginalDesign(
        requestBody: CrudOriginalDesignDto,
        tok: String
    ): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) {
            crudOriginalDesignClient.doCrudOriginalDesign(requestBody, tok)
        }
    }
}