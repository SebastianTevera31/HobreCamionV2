package com.rfz.appflotal.data.network.service.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignListResponse
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignListClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadDesignListService @Inject constructor(
    private val retreadDesignListClient: RetreadDesignListClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doCrudRetreadDesignList(): Response<List<RetreadDesignListResponse>> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase.invoke().first()[0].fld_token
            retreadDesignListClient.doRetreadDesignList("bearer $token")
        }
    }
}