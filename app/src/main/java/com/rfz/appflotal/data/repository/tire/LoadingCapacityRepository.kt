package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.tire.response.LoadingCapacityResponse
import com.rfz.appflotal.data.network.client.tire.LoadingCapacityClient
import com.rfz.appflotal.data.network.service.tire.LoadingCapacityService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class LoadingCapacityRepository @Inject constructor(private val loadingCapacityService: LoadingCapacityService) {
    suspend fun doLoadCapacity(id_user: Int, tok:String): Response<List<LoadingCapacityResponse>> {
        return withContext(Dispatchers.IO) {
            loadingCapacityService.doLoadCapacity(id_user,tok)
        }
    }
}