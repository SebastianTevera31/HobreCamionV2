package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.tire.response.TireIdResponse
import com.rfz.appflotal.data.network.client.tire.TireGetClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireGetService @Inject constructor(private val tireGetClient: TireGetClient) {
    suspend fun doTireGet(idTire: Int, tok:String): Response<List<TireIdResponse>> {
        return withContext(Dispatchers.IO) {
            tireGetClient.doTireGet("Bearer $tok",idTire)
        }
    }
}