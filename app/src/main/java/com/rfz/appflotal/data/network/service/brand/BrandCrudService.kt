package com.rfz.appflotal.data.network.service.brand

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.client.brand.BrandCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class BrandCrudService @Inject constructor(private val brandCrudClient: BrandCrudClient) {

    suspend fun doBrandCrud(requestBody: BrandCrudDto,token:String): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) { brandCrudClient.doBrandCrud(requestBody,token)
        }
    }
}