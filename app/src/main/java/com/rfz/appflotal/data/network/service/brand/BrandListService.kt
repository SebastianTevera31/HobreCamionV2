package com.rfz.appflotal.data.network.service.brand


import com.rfz.appflotal.data.model.brand.response.BranListResponse

import com.rfz.appflotal.data.network.client.brand.BrandListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class BrandListService @Inject constructor(private val brandListClient: BrandListClient) {
    suspend fun doBrandList(tok:String,iduser:Int): Response<List<BranListResponse>> {
        return withContext(Dispatchers.IO) {
            brandListClient.doBrandList(tok,iduser)
        }
    }
}