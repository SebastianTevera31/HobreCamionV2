package com.rfz.appflotal.data.network.service.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ImperfectPairResponse
import com.rfz.appflotal.data.network.client.imperfectpair.ImperfectPairClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ImperfectPairService @Inject constructor(
    private val client: ImperfectPairClient
) {
    suspend fun getImperfectPairs(token: String): Response<List<ImperfectPairResponse>> {
        return withContext(Dispatchers.IO) {
            client.getImperfectPairs(token)
        }
    }
}
