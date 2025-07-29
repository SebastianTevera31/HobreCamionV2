package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.data.network.client.tire.TireSizeClient
import com.rfz.appflotal.data.network.service.tire.TireSizeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireSizeRepository @Inject constructor(private val  tireSizeService: TireSizeService) {
    suspend fun doTireSizes(id_user: Int, tok:String): Response<List<TireSizeResponse>> {
        return withContext(Dispatchers.IO) {
            tireSizeService.doTireSizes(id_user,tok)
        }
    }
}