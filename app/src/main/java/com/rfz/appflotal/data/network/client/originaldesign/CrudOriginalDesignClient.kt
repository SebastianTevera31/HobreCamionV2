package com.rfz.appflotal.data.network.client.originaldesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST



interface CrudOriginalDesignClient {


    @POST("api/Catalog/CrudOriginalDesign")
    suspend fun doCrudOriginalDesign(@Body requestBody: CrudOriginalDesignDto, @Header("Authorization") token: String): Response<List<MessageResponse>>
}