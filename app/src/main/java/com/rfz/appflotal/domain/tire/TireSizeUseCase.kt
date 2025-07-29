package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import com.rfz.appflotal.data.network.service.tire.TireSizeService
import com.rfz.appflotal.data.repository.tire.TireSizeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireSizeUseCase @Inject constructor(private val  tireSizeRepository: TireSizeRepository) {
    suspend fun doTireSizes(id_user: Int, tok:String): Response<List<TireSizeResponse>> {
        return withContext(Dispatchers.IO) {
            tireSizeRepository.doTireSizes(id_user,tok)
        }
    }
}