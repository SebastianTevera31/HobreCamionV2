package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.data.network.client.tire.TireGetClient
import com.rfz.appflotal.data.network.client.tire.TireSizeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class TireSizeService @Inject constructor(private val tireSizeClient: TireSizeClient) {
    suspend fun doTireSizes(id_user: Int, tok:String): Response<List<TireSizeResponse>> {
        return withContext(Dispatchers.IO) {
            tireSizeClient.getTireSizes(id_user,tok)
        }
    }
}