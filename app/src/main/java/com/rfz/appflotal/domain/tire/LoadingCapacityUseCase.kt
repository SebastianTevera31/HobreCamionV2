package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.tire.response.LoadingCapacityResponse
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.data.repository.tire.LoadingCapacityRepository
import com.rfz.appflotal.data.repository.tire.TireSizeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class LoadingCapacityUseCase @Inject constructor(private val  loadingCapacityRepository: LoadingCapacityRepository) {
    suspend fun doCapacity(id_user: Int, tok:String): Response<List<LoadingCapacityResponse>> {
        return withContext(Dispatchers.IO) {
            loadingCapacityRepository.doLoadCapacity(id_user,tok)
        }
    }
}