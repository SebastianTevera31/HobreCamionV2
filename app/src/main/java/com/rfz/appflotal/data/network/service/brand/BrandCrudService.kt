package com.rfz.appflotal.data.network.service.brand

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.login.dto.LoginDto
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.client.brand.BrandCrudClient
import com.rfz.appflotal.data.network.client.login.LoginClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class BrandCrudService @Inject constructor(private val brandCrudClient: BrandCrudClient) {

    suspend fun doBrandCrud(requestBody: BrandCrudDto,token:String): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) { brandCrudClient.doBrandCrud(requestBody,token)
        }
    }
}