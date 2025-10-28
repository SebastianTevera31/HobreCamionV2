package com.rfz.appflotal.data.network.service.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignByIdResponse
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignByIdClient
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RetreadDesignByIdService @Inject constructor(
    private val retreadDesignByIdClient: RetreadDesignByIdClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun onGetRetreadDesignById(retreadDesignId: Int): Result<RetreadDesignByIdResponse> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase.invoke().first()[0].fld_token
            retreadDesignByIdClient.onRetreadDesignById("bearer $token", retreadDesignId)
        }
    }
}