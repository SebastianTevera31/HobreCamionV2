package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.tire.response.LoadingCapacityResponse
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.data.network.client.tire.LoadingCapacityClient
import com.rfz.appflotal.data.network.client.tire.TireSizeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class LoadingCapacityService @Inject constructor(private val loadingCapacityClient: LoadingCapacityClient) {
    suspend fun doLoadCapacity(id_user: Int, tok:String): Response<List<LoadingCapacityResponse>> {
        return withContext(Dispatchers.IO) {
            loadingCapacityClient.getLoadCapacity(id_user,tok)
        }
    }
}